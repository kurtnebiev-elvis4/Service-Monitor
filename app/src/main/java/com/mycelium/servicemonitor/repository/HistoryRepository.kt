package com.mycelium.servicemonitor.repository

import com.mycelium.servicemonitor.model.CheckHistoryEntity
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HistoryRepository @Inject constructor(private val historyDao: CheckHistoryDao) {
    suspend fun insert(item: CheckHistoryEntity) {
        historyDao.insert(item)
    }

    suspend fun getAll(offset: Int = 0, limit: Int = 50): List<CheckHistoryEntity> {
        return historyDao.getAll()
    }
}