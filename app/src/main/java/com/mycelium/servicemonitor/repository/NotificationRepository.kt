package com.mycelium.servicemonitor.repository

import com.mycelium.servicemonitor.model.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(private val notificationDao: NotificationDao) {
    suspend fun insert(notification: NotificationEntity) {
        notificationDao.insert(notification)
    }

    fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }

    suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }
} 