package com.neverforget.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "summaries")
data class SummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    @ColumnInfo(name = "summary_content")
    val summaryContent: String,

    @ColumnInfo(name = "original_message_ids")
    val originalMessageIds: String, // JSON array of Message.id

    @ColumnInfo(name = "source_app")
    val sourceApp: String,

    @ColumnInfo(name = "conversation_name")
    val conversationName: String,

    @ColumnInfo(name = "date_range")
    val dateRange: String,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
