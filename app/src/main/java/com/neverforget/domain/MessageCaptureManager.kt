package com.neverforget.domain

import com.neverforget.data.local.entity.MessageEntity
import com.neverforget.repository.MessageRepository
import com.neverforget.util.HashUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageCaptureManager @Inject constructor(
    private val messageRepository: MessageRepository
) {
    private var isCapturing = false

    fun isCapturing(): Boolean = isCapturing

    fun startCapture() {
        isCapturing = true
    }

    fun stopCapture() {
        isCapturing = false
    }

    suspend fun captureMessage(
        sourceApp: String,
        conversationName: String,
        conversationType: String,
        senderName: String,
        content: String,
        timestamp: Long
    ): Long {
        val message = MessageEntity(
            sourceApp = sourceApp,
            conversationName = conversationName,
            conversationType = conversationType,
            senderName = senderName,
            content = content,
            timestamp = timestamp,
            messageHash = HashUtils.messageHash(
                sourceApp, conversationName, senderName, content, timestamp
            )
        )
        return messageRepository.insertMessage(message)
    }
}
