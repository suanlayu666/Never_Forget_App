package com.neverforget.domain

import com.neverforget.data.local.entity.MessageEntity
import com.neverforget.data.local.entity.SummaryEntity
import com.neverforget.data.preferences.AppPreferences
import com.neverforget.repository.MessageRepository
import com.neverforget.repository.SummaryRepository
import com.neverforget.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryEngine @Inject constructor(
    private val messageRepository: MessageRepository,
    private val summaryRepository: SummaryRepository,
    private val appPreferences: AppPreferences,
    private val okHttpClient: OkHttpClient,
    private val notificationHelper: NotificationHelper
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    data class SummaryResult(
        val summary: SummaryEntity,
        val success: Boolean,
        val error: String? = null
    )

    suspend fun generateSummary(conversationNames: List<String>? = null): SummaryResult = withContext(Dispatchers.IO) {
        try {
            // 1. 获取配置
            val apiUrl = appPreferences.aiApiUrl.first()
            val apiKey = appPreferences.aiApiKey.first()
            val model = appPreferences.aiModel.first()

            if (apiKey.isBlank()) {
                return@withContext SummaryResult(
                    SummaryEntity(title = "", summaryContent = "", originalMessageIds = "", sourceApp = "", conversationName = "", dateRange = ""),
                    false, "请先在设置中配置 AI API Key"
                )
            }

            // 2. 获取未摘要消息
            val unsummarized = messageRepository.getUnsummarizedMessages()
            if (unsummarized.isEmpty()) {
                return@withContext SummaryResult(
                    SummaryEntity(title = "", summaryContent = "", originalMessageIds = "", sourceApp = "", conversationName = "", dateRange = ""),
                    false, "没有需要摘要的消息"
                )
            }

            // 3. 按会话分组，如果指定了会话列表则合并这些会话，否则选消息最多的
            val grouped = unsummarized.groupBy { it.conversationName }
            val (finalConversationName, messages, sourceApp) = if (!conversationNames.isNullOrEmpty()) {
                // 多选：合并指定会话中的所有消息
                val selected = conversationNames.flatMap { grouped[it] ?: emptyList() }.sortedBy { it.timestamp }
                if (selected.isEmpty()) {
                    return@withContext SummaryResult(
                        SummaryEntity(title = "", summaryContent = "", originalMessageIds = "", sourceApp = "", conversationName = "", dateRange = ""),
                        false, "所选会话无未摘要消息"
                    )
                }
                val apps = selected.map { it.sourceApp }.distinct()
                Triple(
                    conversationNames.joinToString("、"),
                    selected,
                    apps.joinToString("/")
                )
            } else {
                // 自动选最多消息的
                val best = grouped.maxByOrNull { it.value.size }
                    ?: return@withContext SummaryResult(
                        SummaryEntity(title = "", summaryContent = "", originalMessageIds = "", sourceApp = "", conversationName = "", dateRange = ""),
                        false, "分组失败"
                    )
                Triple(best.key, best.value.sortedBy { it.timestamp }, best.value.first().sourceApp)
            }

            // 4. 构建消息文本
            val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
            val messagesText = messages.joinToString("\n") { msg ->
                "[${sdf.format(Date(msg.timestamp))}] ${msg.senderName}：${msg.content}"
            }

            // 5. 构建提示词
            val promptTemplate = appPreferences.aiPromptTemplate.first()
            val prompt = promptTemplate.replace("{messages}", messagesText)

            // 6. 调用 AI API
            val requestBody = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "你是一个专业的消息整理助手，擅长提炼关键信息和生成结构化摘要。")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("temperature", 0.7)
            }

            val url = if (apiUrl.endsWith("/")) apiUrl + "chat/completions"
                      else apiUrl + "/chat/completions"

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody(jsonMediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext SummaryResult(
                    SummaryEntity(title = "", summaryContent = "", originalMessageIds = "", sourceApp = "", conversationName = "", dateRange = ""),
                    false, "API 调用失败 (${response.code}): $responseBody"
                )
            }

            // 7. 解析响应
            val json = JSONObject(responseBody)
            val choices = json.getJSONArray("choices")
            val rawContent = choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            // 8. 清理 AI 返回内容：去除 JSON/代码块包装
            var content = rawContent.trim()
            // 去除 ```json ... ``` 包装
            if (content.startsWith("```")) {
                content = content.removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()
            }
            // 如果返回的是纯 JSON 对象，尝试提取其中的文本值拼接
            if (content.startsWith("{") && content.endsWith("}")) {
                try {
                    val jsonContent = JSONObject(content)
                    val parts = mutableListOf<String>()
                    for (key in jsonContent.keys()) {
                        val value = jsonContent.opt(key)
                        parts.add("$value")
                    }
                    if (parts.isNotEmpty()) {
                        content = parts.joinToString("\n")
                    }
                } catch (_: Exception) {}
            }

            // 9. 提取标题：找第一行有意义的文字（跳过 { [ # ``` 等符号行）
            val lines = content.split("\n")
                .map { it.trim() }
                .filter { line ->
                    line.isNotBlank() &&
                    !line.startsWith("{") && !line.startsWith("}") &&
                    !line.startsWith("[") && !line.startsWith("```") &&
                    line.length > 2
                }
            val title = lines.firstOrNull()
                ?.replace(Regex("^[#*\\-\\d.]+\\s*"), "")  // 去掉 markdown 标题标记和序号
                ?.trim()
                ?.take(50)
                ?: "消息摘要"

            // 同时清理 content 中的 JSON 残留符号，保留纯文本
            content = content
                .replace("{", "").replace("}", "")
                .replace("\"", "")
                .trim()

            // 9. 计算日期范围
            val dateRange = if (messages.size > 1) {
                "${sdf.format(Date(messages.first().timestamp))} ~ ${sdf.format(Date(messages.last().timestamp))}"
            } else {
                sdf.format(Date(messages.first().timestamp))
            }

            // 10. 存入数据库
            val summary = SummaryEntity(
                title = title,
                summaryContent = content.trim(),
                originalMessageIds = messages.joinToString(",") { it.id.toString() },
                sourceApp = sourceApp,
                conversationName = finalConversationName,
                dateRange = dateRange,
                isRead = false,
                createdAt = System.currentTimeMillis()
            )
            val savedId = summaryRepository.insertSummary(summary)

            // 11. 标记消息已摘要
            messageRepository.markAsSummarized(messages.map { it.id })

            // 12. 发送通知
            notificationHelper.showSummaryNotification(savedId, title, content.trim())

            SummaryResult(summary.copy(id = savedId), true)
        } catch (e: Exception) {
            SummaryResult(
                SummaryEntity(title = "", summaryContent = "", originalMessageIds = "", sourceApp = "", conversationName = "", dateRange = ""),
                false, "摘要生成失败: ${e.message}"
            )
        }
    }
}
