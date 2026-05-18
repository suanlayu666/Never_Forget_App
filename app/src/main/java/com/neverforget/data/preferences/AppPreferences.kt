package com.neverforget.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "neverforget_prefs")

class AppPreferences(private val context: Context) {

    companion object {
        // AI API 配置
        val KEY_AI_API_URL = stringPreferencesKey("ai_api_url")
        val KEY_AI_API_KEY = stringPreferencesKey("ai_api_key")
        val KEY_AI_MODEL = stringPreferencesKey("ai_model")
        val KEY_AI_PROMPT_TEMPLATE = stringPreferencesKey("ai_prompt_template")

        // 抓取配置
        val KEY_CAPTURE_WECHAT = booleanPreferencesKey("capture_wechat")
        val KEY_CAPTURE_WEWORK = booleanPreferencesKey("capture_wework")
        val KEY_CAPTURE_QQ = booleanPreferencesKey("capture_qq")

        // 摘要配置
        val KEY_AUTO_SUMMARY_ENABLED = booleanPreferencesKey("auto_summary_enabled")
        val KEY_AUTO_SUMMARY_INTERVAL = intPreferencesKey("auto_summary_interval") // 分钟
        val KEY_AUTO_SUMMARY_MESSAGE_COUNT = intPreferencesKey("auto_summary_message_count")

        // Mock 数据
        val KEY_MOCK_SEEDED = booleanPreferencesKey("mock_seeded")

        // 通知配置
        val KEY_NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")

        // 默认值
        const val DEFAULT_AI_API_URL = "https://api.openai.com/v1"
        const val DEFAULT_AI_MODEL = "gpt-4o"
        const val DEFAULT_AUTO_SUMMARY_INTERVAL = 60
        const val DEFAULT_AUTO_SUMMARY_MESSAGE_COUNT = 50
        const val DEFAULT_PROMPT_TEMPLATE = "你是一个专业消息整理助手。请对以下聊天记录整理总结：\n" +
                "1. 关键讨论话题（不超过5个）\n" +
                "2. 需要跟进的事项\n" +
                "3. 重要决定或结论\n" +
                "4. 涉及时间、地点等安排\n\n" +
                "聊天记录：\n{messages}\n\n" +
                "请用纯中文文本格式输出，不要用JSON或代码块。格式：\n" +
                "摘要标题（一行）\n" +
                "关键话题\n" +
                "1. xxx\n" +
                "2. xxx\n" +
                "待办事项\n" +
                "- xxx\n" +
                "- xxx\n" +
                "结论\n" +
                "- xxx"
    }

    // AI 配置
    val aiApiUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_AI_API_URL] ?: DEFAULT_AI_API_URL
    }

    val aiApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_AI_API_KEY] ?: ""
    }

    val aiModel: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_AI_MODEL] ?: DEFAULT_AI_MODEL
    }

    val aiPromptTemplate: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_AI_PROMPT_TEMPLATE] ?: DEFAULT_PROMPT_TEMPLATE
    }

    // 抓取配置
    val captureWechat: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_CAPTURE_WECHAT] ?: true
    }

    val captureWeWork: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_CAPTURE_WEWORK] ?: true
    }

    val captureQq: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_CAPTURE_QQ] ?: true
    }

    // 摘要配置
    val autoSummaryEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_SUMMARY_ENABLED] ?: false
    }

    val autoSummaryInterval: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_SUMMARY_INTERVAL] ?: DEFAULT_AUTO_SUMMARY_INTERVAL
    }

    val autoSummaryMessageCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_SUMMARY_MESSAGE_COUNT] ?: DEFAULT_AUTO_SUMMARY_MESSAGE_COUNT
    }

    val notificationEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATION_ENABLED] ?: true
    }

    val isMockSeeded: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_MOCK_SEEDED] ?: false
    }

    // 写入方法
    suspend fun setAiApiUrl(url: String) {
        context.dataStore.edit { it[KEY_AI_API_URL] = url }
    }

    suspend fun setAiApiKey(key: String) {
        context.dataStore.edit { it[KEY_AI_API_KEY] = key }
    }

    suspend fun setAiModel(model: String) {
        context.dataStore.edit { it[KEY_AI_MODEL] = model }
    }

    suspend fun setAiPromptTemplate(template: String) {
        context.dataStore.edit { it[KEY_AI_PROMPT_TEMPLATE] = template }
    }

    suspend fun setCaptureWechat(enabled: Boolean) {
        context.dataStore.edit { it[KEY_CAPTURE_WECHAT] = enabled }
    }

    suspend fun setCaptureWeWork(enabled: Boolean) {
        context.dataStore.edit { it[KEY_CAPTURE_WEWORK] = enabled }
    }

    suspend fun setCaptureQq(enabled: Boolean) {
        context.dataStore.edit { it[KEY_CAPTURE_QQ] = enabled }
    }

    suspend fun setAutoSummaryEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_SUMMARY_ENABLED] = enabled }
    }

    suspend fun setAutoSummaryInterval(minutes: Int) {
        context.dataStore.edit { it[KEY_AUTO_SUMMARY_INTERVAL] = minutes }
    }

    suspend fun setAutoSummaryMessageCount(count: Int) {
        context.dataStore.edit { it[KEY_AUTO_SUMMARY_MESSAGE_COUNT] = count }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFICATION_ENABLED] = enabled }
    }

    suspend fun setMockSeeded(seeded: Boolean) {
        context.dataStore.edit { it[KEY_MOCK_SEEDED] = seeded }
    }
}
