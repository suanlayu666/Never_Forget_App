package com.neverforget.repository

import com.neverforget.data.local.dao.SummaryDao
import com.neverforget.data.local.entity.SummaryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRepository @Inject constructor(
    private val summaryDao: SummaryDao
) {
    fun getAllSummaries(): Flow<List<SummaryEntity>> = summaryDao.getAllSummaries()

    fun getSummariesByApp(sourceApp: String): Flow<List<SummaryEntity>> =
        summaryDao.getSummariesByApp(sourceApp)

    fun getUnreadCount(): Flow<Int> = summaryDao.getUnreadCount()

    suspend fun getSummaryById(id: Long): SummaryEntity? = summaryDao.getSummaryById(id)

    suspend fun insertSummary(summary: SummaryEntity): Long = summaryDao.insert(summary)

    suspend fun markAsRead(id: Long) = summaryDao.markAsRead(id)

    suspend fun deleteAll() = summaryDao.deleteAll()
}
