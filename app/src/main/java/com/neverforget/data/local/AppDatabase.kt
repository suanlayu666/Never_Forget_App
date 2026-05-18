package com.neverforget.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neverforget.data.local.dao.MessageDao
import com.neverforget.data.local.dao.SummaryDao
import com.neverforget.data.local.entity.MessageEntity
import com.neverforget.data.local.entity.SummaryEntity

@Database(
    entities = [MessageEntity::class, SummaryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun summaryDao(): SummaryDao
}
