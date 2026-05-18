package com.neverforget.repository

import com.neverforget.data.local.dao.MessageDao
import com.neverforget.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val messageDao: MessageDao
) {
    fun getAllMessages(): Flow<List<MessageEntity>> = messageDao.getAllMessages()

    fun getMessagesByApp(sourceApp: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesByApp(sourceApp)

    fun getMessagesByConversation(conversationName: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesByConversation(conversationName)

    fun getUnsummarizedCount(): Flow<Int> = messageDao.getUnsummarizedCount()

    fun getConversationsByApp(sourceApp: String): Flow<List<String>> =
        messageDao.getConversationsByApp(sourceApp)

    suspend fun insertMessage(message: MessageEntity): Long = messageDao.insert(message)

    suspend fun insertMessages(messages: List<MessageEntity>): List<Long> =
        messageDao.insertAll(messages)

    suspend fun getUnsummarizedMessages(): List<MessageEntity> =
        messageDao.getUnsummarizedMessages()

    suspend fun getUnsummarizedMessagesByApp(sourceApp: String): List<MessageEntity> =
        messageDao.getUnsummarizedMessagesByApp(sourceApp)

    suspend fun markAsSummarized(messageIds: List<Long>) =
        messageDao.markAsSummarized(messageIds)

    suspend fun deleteAll() = messageDao.deleteAll()
}
