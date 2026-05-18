package com.neverforget.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.neverforget.data.local.entity.MessageEntity
import com.neverforget.repository.MessageRepository
import com.neverforget.util.HashUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MessageAccessibilityService : AccessibilityService() {

    @Inject lateinit var messageRepository: MessageRepository
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        @Volatile var shouldScan = false
        @Volatile var lastScanCount = -1
        @Volatile var lastScanError: String? = null
        @Volatile var isRunning = false
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isRunning = true
        // 启动轮询：每 300ms 检查是否需要扫描
        scope.launch {
            while (isActive) {
                if (shouldScan) {
                    shouldScan = false
                    scope.launch { performScan() }
                }
                delay(300)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        isRunning = false
        scope.cancel()
        super.onDestroy()
    }

    private suspend fun performScan() {
        try {
            lastScanError = null

            val root: AccessibilityNodeInfo = rootInActiveWindow ?: run {
                lastScanError = "无法获取窗口内容。请确认无障碍服务已开启，且当前在聊天界面"
                lastScanCount = 0
                return
            }

            // 检测当前 App
            val sourceApp = when {
                root.packageName?.toString()?.contains("tencent.mm") == true -> "wechat"
                root.packageName?.toString()?.contains("wework") == true -> "wework"
                root.packageName?.toString()?.contains("mobileqq") == true -> "qq"
                else -> "unknown"
            }

            val appLabel = when (sourceApp) {
                "wechat" -> "微信"
                "wework" -> "企业微信"
                "qq" -> "QQ"
                else -> "未知"
            }

            // 提取所有可见文本
            val allTexts = mutableListOf<String>()
            extractAllText(root, allTexts)

            // 尝试从标题栏检测会话名（前几个文本节点中排除 UI 标签）
            val uiLabels = setOf(
                "微信", "WeChat", "通讯录", "发现", "我", "消息",
                "返回", "更多", "设置", "搜索", "扫一扫", "小程序", "视频号",
                "朋友圈", "视频", "直播", "购物", "游戏", "看一看", "搜一搜",
                "联系人", "工作台", "文档", "日程", "会议", "审批",
                "QQ", "动态", "看点", "短视频", "频道", "空间",
                "发送", "取消", "确定", "复制", "转发", "收藏", "删除", "撤回",
                "语音", "图片", "视频通话", "语音通话", "位置", "红包", "转账",
                "文件", "名片", "相册", "拍摄", "卡券",
                "表情", "贴纸", "GIF", "语音输入", "更多功能", "企业微信",
            )
            val titleCandidates = allTexts
                .filter { it.length in 2..25 }
                .filterNot { uiLabels.contains(it) }
                .filterNot { it.matches(Regex("^\\d{1,2}:\\d{2}$")) }
                .filterNot { it.matches(Regex("^\\d+$")) }
                .filterNot { it.matches(Regex("^\\d+条|\\d+人|在线|离线|输入中$")) }
            val detectedTitle = titleCandidates.firstOrNull() ?: ""

            // 生成扫描会话名
            val sdf = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
            val timeLabel = sdf.format(java.util.Date())
            val scanConversation = if (detectedTitle.isNotEmpty()) {
                "[$appLabel] $detectedTitle"
            } else {
                "[$appLabel] 扫描 $timeLabel"
            }

            root.recycle()

            if (allTexts.isEmpty()) {
                lastScanError = "未检测到文字。请确认已在聊天界面"
                lastScanCount = 0
                return
            }

            // 过滤 UI 标签
            val uiLabelsFull = uiLabels + setOf(
                "微信", "WeChat", "企业微信", "通讯录", "发现", "我", "消息",
                "返回", "更多", "设置", "搜索", "扫一扫", "小程序", "视频号",
                "朋友圈", "视频", "直播", "购物", "游戏", "看一看", "搜一搜",
                "联系人", "工作台", "文档", "日程", "会议", "审批",
                "QQ", "动态", "看点", "短视频", "频道", "联系人", "动态", "空间",
                "发送", "取消", "确定", "复制", "转发", "收藏", "删除", "撤回",
                "语音", "图片", "视频通话", "语音通话", "位置", "红包", "转账",
                "文件", "名片", "收藏", "相册", "拍摄", "红包", "卡券",
                "表情", "贴纸", "GIF", "语音输入", "更多功能",
            )

            val texts = allTexts
                .filter { it.length >= 2 }
                .filter { t -> uiLabelsFull.none { t.contains(it) && t.length < 6 } }
                .filterNot { it.startsWith("http") }
                .filterNot { it.matches(Regex("^\\d{1,2}:\\d{2}$")) }
                .filterNot { it.matches(Regex("^\\d+$")) }
                .filterNot { it.matches(Regex("^[a-fA-F0-9]{4,}$")) }
                .filterNot { it == detectedTitle } // 排除会话名本身

            if (texts.isEmpty()) {
                lastScanError = "过滤后无有效文字（原始 ${allTexts.size} 段）。请确认在聊天界面。"
                lastScanCount = 0
                return
            }

            // 配对发送者+内容
            val now = System.currentTimeMillis()
            val insertedHashes = mutableSetOf<String>()
            var inserted = 0
            var i = 0

            while (i < texts.size) {
                val first = texts[i]
                // 如果当前段像发送者昵称（短文本），且下一段存在
                if (first.length in 2..16 && i + 1 < texts.size) {
                    val sender = first
                    val content = texts[i + 1]
                    val hash = HashUtils.messageHash(sourceApp, "扫描会话", sender, content, now + i)
                    if (hash !in insertedHashes) {
                        insertedHashes.add(hash)
                        val id = messageRepository.insertMessage(MessageEntity(
                            sourceApp = sourceApp,
                            conversationName = scanConversation,
                            conversationType = "group",
                            senderName = sender,
                            content = content,
                            timestamp = now - (texts.size - i) * 30000,
                            messageHash = hash,
                            isSummarized = false
                        ))
                        if (id > 0) inserted++
                    }
                    i += 2
                } else {
                    // 独立的长文本
                    if (first.length > 4) {
                        val hash = HashUtils.messageHash(sourceApp, "扫描会话", "", first, now + i)
                        if (hash !in insertedHashes) {
                            insertedHashes.add(hash)
                            val id = messageRepository.insertMessage(MessageEntity(
                                sourceApp = sourceApp,
                                conversationName = scanConversation,
                                conversationType = "group",
                                senderName = "",
                                content = first,
                                timestamp = now - (texts.size - i) * 30000,
                                messageHash = hash,
                                isSummarized = false
                            ))
                            if (id > 0) inserted++
                        }
                    }
                    i++
                }
            }

            lastScanCount = inserted
            if (inserted == 0) {
                lastScanError = "扫描到 ${texts.size} 段文字但无法识别为消息"
            }
        } catch (e: Exception) {
            lastScanError = "扫描异常: ${e.message}"
            lastScanCount = 0
        }
    }

    private fun extractAllText(node: AccessibilityNodeInfo, result: MutableList<String>) {
        if (node.text != null && node.text.isNotEmpty()) {
            val text = node.text.toString().trim()
            if (text.length >= 2) result.add(text)
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            extractAllText(child, result)
            child.recycle()
        }
    }
}
