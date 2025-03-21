package com.mycelium.servicemonitor.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycelium.servicemonitor.model.CheckHistoryEntity
import com.mycelium.servicemonitor.model.NotificationEntity
import com.mycelium.servicemonitor.model.ServiceEntity
import com.mycelium.servicemonitor.repository.CheckHistoryDao
import com.mycelium.servicemonitor.repository.NotificationDao
import com.mycelium.servicemonitor.repository.ServiceDao

@Database(
    entities = [ServiceEntity::class, CheckHistoryEntity::class, NotificationEntity::class], 
    version = 8, 
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
    abstract fun historyDao(): CheckHistoryDao
    abstract fun notificationDao(): NotificationDao
}