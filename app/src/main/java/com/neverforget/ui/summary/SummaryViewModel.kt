package com.neverforget.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neverforget.data.local.entity.MessageEntity
import com.neverforget.data.local.entity.SummaryEntity
import com.neverforget.domain.SummaryEngine
import com.neverforget.repository.MessageRepository
import com.neverforget.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SummaryWithMessages(
    val summary: SummaryEntity,
    val originalMessages: List<MessageEntity>
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
    private val messageRepository: MessageRepository,
    private val summaryEngine: SummaryEngine
) : ViewModel() {

    private val _filterApp = MutableStateFlow<String?>(null)
    private val _generating = MutableStateFlow(false)
    private val _lastMessage = MutableStateFlow<String?>(null)

    val filterApp: StateFlow<String?> = _filterApp.asStateFlow()
    val isGenerating: StateFlow<Boolean> = _generating.asStateFlow()
    val lastMessage: StateFlow<String?> = _lastMessage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val summaries: StateFlow<List<SummaryEntity>> = _filterApp
        .flatMapLatest { app ->
            summaryRepository.getAllSummaries()
        }
        .combine(_filterApp) { summaries, appFilter ->
            if (appFilter != null) {
                summaries.filter { it.sourceApp == appFilter }
            } else {
                summaries
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = summaryRepository.getUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // 从消息表获取有未摘要消息的会话列表
    @OptIn(ExperimentalCoroutinesApi::class)
    val availableConversations: StateFlow<List<Pair<String, String>>> = messageRepository.getAllMessages()
        .map { messages ->
            messages
                .filter { !it.isSummarized }
                .map { it.conversationName to it.sourceApp }
                .distinct()
                .sortedBy { it.first }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(app: String?) {
        _filterApp.value = app
    }

    fun generateSummary(conversationNames: List<String>? = null) {
        if (_generating.value) return
        viewModelScope.launch {
            _generating.value = true
            _lastMessage.value = "正在生成摘要..."
            val result = summaryEngine.generateSummary(conversationNames)
            _generating.value = false
            if (result.success) {
                _lastMessage.value = "摘要生成成功：${result.summary.title}"
            } else {
                _lastMessage.value = result.error
            }
        }
    }

    fun clearMessage() {
        _lastMessage.value = null
    }

    suspend fun getSummaryWithMessages(summaryId: Long): SummaryWithMessages? {
        val summary = summaryRepository.getSummaryById(summaryId) ?: return null
        if (!summary.isRead) {
            summaryRepository.markAsRead(summaryId)
        }
        val messageIds = summary.originalMessageIds
            .split(",")
            .mapNotNull { it.trim().toLongOrNull() }
        val allMessages = messageRepository.getAllMessages()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
            .value
        val originalMessages = allMessages.filter { it.id in messageIds }
        return SummaryWithMessages(summary, originalMessages)
    }
}
