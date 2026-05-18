package com.neverforget.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neverforget.data.local.entity.SummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(summary: SummaryEntity): Long

    @Query("SELECT * FROM summaries ORDER BY created_at DESC")
    fun getAllSummaries(): Flow<List<SummaryEntity>>

    @Query("SELECT * FROM summaries WHERE id = :id")
    suspend fun getSummaryById(id: Long): SummaryEntity?

    @Query("SELECT * FROM summaries WHERE source_app = :sourceApp ORDER BY created_at DESC")
    fun getSummariesByApp(sourceApp: String): Flow<List<SummaryEntity>>

    @Query("SELECT COUNT(*) FROM summaries WHERE is_read = 0")
    fun getUnreadCount(): Flow<Int>

    @Query("UPDATE summaries SET is_read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("DELETE FROM summaries")
    suspend fun deleteAll()
}
