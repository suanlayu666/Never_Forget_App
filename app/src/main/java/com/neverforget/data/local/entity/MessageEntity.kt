package com.neverforget.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [Index(value = ["message_hash"], unique = true)]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "source_app")
    val sourceApp: String, // "wechat" | "wework" | "qq"

    @ColumnInfo(name = "conversation_name")
    val conversationName: String,

    @ColumnInfo(name = "conversation_type")
    val conversationType: String, // "group" | "private"

    @ColumnInfo(name = "sender_name")
    val senderName: String,

    val content: String,

    val timestamp: Long,

    @ColumnInfo(name = "message_hash")
    val messageHash: String,

    @ColumnInfo(name = "is_summarized")
    val isSummarized: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
