package com.neverforget.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neverforget.data.local.entity.MessageEntity
import com.neverforget.data.preferences.AppPreferences
import com.neverforget.repository.MessageRepository
import com.neverforget.repository.SummaryRepository
import com.neverforget.util.HashUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val messageRepository: MessageRepository,
    private val summaryRepository: SummaryRepository
) : ViewModel() {

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    val aiApiUrl = appPreferences.aiApiUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppPreferences.DEFAULT_AI_API_URL)

    val aiApiKey = appPreferences.aiApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val aiModel = appPreferences.aiModel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppPreferences.DEFAULT_AI_MODEL)

    val captureWechat = appPreferences.captureWechat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val captureWeWork = appPreferences.captureWeWork
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val captureQq = appPreferences.captureQq
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val autoSummaryEnabled = appPreferences.autoSummaryEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val autoSummaryInterval = appPreferences.autoSummaryInterval
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppPreferences.DEFAULT_AUTO_SUMMARY_INTERVAL)

    val autoSummaryMessageCount = appPreferences.autoSummaryMessageCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppPreferences.DEFAULT_AUTO_SUMMARY_MESSAGE_COUNT)

    val notificationEnabled = appPreferences.notificationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setAiApiUrl(url: String) = viewModelScope.launch { appPreferences.setAiApiUrl(url) }
    fun setAiApiKey(key: String) = viewModelScope.launch { appPreferences.setAiApiKey(key) }
    fun setAiModel(model: String) = viewModelScope.launch { appPreferences.setAiModel(model) }
    fun setCaptureWechat(enabled: Boolean) = viewModelScope.launch { appPreferences.setCaptureWechat(enabled) }
    fun setCaptureWeWork(enabled: Boolean) = viewModelScope.launch { appPreferences.setCaptureWeWork(enabled) }
    fun setCaptureQq(enabled: Boolean) = viewModelScope.launch { appPreferences.setCaptureQq(enabled) }
    fun setAutoSummaryEnabled(enabled: Boolean) = viewModelScope.launch { appPreferences.setAutoSummaryEnabled(enabled) }
    fun setAutoSummaryInterval(minutes: Int) = viewModelScope.launch { appPreferences.setAutoSummaryInterval(minutes) }
    fun setAutoSummaryMessageCount(count: Int) = viewModelScope.launch { appPreferences.setAutoSummaryMessageCount(count) }
    fun setNotificationEnabled(enabled: Boolean) = viewModelScope.launch { appPreferences.setNotificationEnabled(enabled) }

    fun deleteAllMessages() = viewModelScope.launch { messageRepository.deleteAll() }
    fun deleteAllSummaries() = viewModelScope.launch { summaryRepository.deleteAll() }

    fun createTestMessages() = viewModelScope.launch {
        // 清空全部数据
        summaryRepository.deleteAll()
        messageRepository.deleteAll()

        val now = System.currentTimeMillis()
        val messages = mutableListOf<MessageEntity>()
        val senders = listOf("小王", "设计师", "后端老张", "测试小明", "项目经理")
        val contents = listOf(
            "这个迭代的首页改版需求确认了，本周五之前要上线",
            "首页的 banner 图我更新了一版，大家看看效果",
            "颜色感觉不太对，品牌色用的是 #1890FF，这个偏深了",
            "接口文档更新了，新增了活动列表的筛选参数，后端已经部署到测试环境",
            "收到，今天下午开始测",
            "周五上线的话，周四晚上要封版，大家注意下时间节点",
            "好的，我今晚改完配色发给后端切图",
            "大家加油，这个版本是 Q2 的重点项目，客户那边很重视",
        )

        // 新建 2 个不同会话的消息
        val conv1 = "产品讨论群"
        val conv2 = "技术方案群"
        var ts = now - 120_000

        contents.forEachIndexed { i, content ->
            val conv = if (i < 5) conv1 else conv2
            val sender = senders[i % senders.size]
            ts -= 30_000 // 每条消息间隔 30 秒
            messages.add(
                MessageEntity(
                    sourceApp = "wechat",
                    conversationName = conv,
                    conversationType = "group",
                    senderName = sender,
                    content = content,
                    timestamp = ts,
                    messageHash = HashUtils.messageHash("wechat", conv, sender, content, ts),
                    isSummarized = false
                )
            )
        }

        // 逐条插入，避免 insertAll 静默失败
        var count = 0
        for (msg in messages) {
            val id = messageRepository.insertMessage(msg)
            if (id > 0) count++
        }
        _snackbarEvent.emit("已添加 $count 条测试消息（含 2 个会话），可前往摘要页生成 AI 摘要")
    }
}
