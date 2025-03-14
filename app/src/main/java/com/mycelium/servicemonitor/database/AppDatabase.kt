package com.mycelium.servicemonitor.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycelium.servicemonitor.model.CheckHistoryEntity
import com.mycelium.servicemonitor.model.ServiceEntity
import com.mycelium.servicemonitor.repository.CheckHistoryDao
import com.mycelium.servicemonitor.repository.ServiceDao

@Database(
    entities = [ServiceEntity::class, CheckHistoryEntity::class], version = 5, autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
    abstract fun historyDao(): CheckHistoryDao
}