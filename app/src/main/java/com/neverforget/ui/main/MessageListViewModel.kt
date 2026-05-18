package com.neverforget.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neverforget.data.local.entity.MessageEntity
import com.neverforget.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationGroup(
    val app: String,
    val name: String,
    val type: String, // "group" | "private"
    val messageCount: Int,
    val summarizedCount: Int,
    val latestMessages: List<MessageEntity>,
    val latestTime: Long
)

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _filterApp = MutableStateFlow<String?>(null) // null = 全部

    val filterApp: StateFlow<String?> = _filterApp.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val conversations: StateFlow<List<ConversationGroup>> = _filterApp
        .flatMapLatest { app ->
            messageRepository.getAllMessages()
        }
        .combine(_filterApp) { messages, appFilter ->
            val filtered = if (appFilter != null) {
                messages.filter { it.sourceApp == appFilter }
            } else {
                messages
            }
            filtered
                .groupBy { Triple(it.sourceApp, it.conversationName, it.conversationType) }
                .map { (key, msgs) ->
                    ConversationGroup(
                        app = key.first,
                        name = key.second,
                        type = key.third,
                        messageCount = msgs.size,
                        summarizedCount = msgs.count { it.isSummarized },
                        latestMessages = msgs.sortedByDescending { it.timestamp }.take(3),
                        latestTime = msgs.maxOf { it.timestamp }
                    )
                }
                .sortedByDescending { it.latestTime }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = messageRepository.getUnsummarizedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setFilter(app: String?) {
        _filterApp.value = app
    }
}
