package com.neverforget.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neverforget.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: MessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(messages: List<MessageEntity>): List<Long>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE source_app = :sourceApp ORDER BY timestamp DESC")
    fun getMessagesByApp(sourceApp: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversation_name = :conversationName ORDER BY timestamp DESC")
    fun getMessagesByConversation(conversationName: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE is_summarized = 0 ORDER BY timestamp DESC")
    suspend fun getUnsummarizedMessages(): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE is_summarized = 0 AND source_app = :sourceApp")
    suspend fun getUnsummarizedMessagesByApp(sourceApp: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE is_summarized = 0 AND conversation_name = :conversationName")
    suspend fun getUnsummarizedMessagesByConversation(conversationName: String): List<MessageEntity>

    @Query("UPDATE messages SET is_summarized = 1 WHERE id IN (:messageIds)")
    suspend fun markAsSummarized(messageIds: List<Long>)

    @Query("SELECT DISTINCT conversation_name FROM messages WHERE source_app = :sourceApp")
    fun getConversationsByApp(sourceApp: String): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM messages WHERE is_summarized = 0")
    fun getUnsummarizedCount(): Flow<Int>

    @Query("DELETE FROM messages")
    suspend fun deleteAll()
}
