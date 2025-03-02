package com.mycelium.servicemonitor.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycelium.servicemonitor.model.CheckHistoryEntity
import com.mycelium.servicemonitor.model.ServiceEntity
import com.mycelium.servicemonitor.repository.CheckHistoryDao
import com.mycelium.servicemonitor.repository.ServiceDao

@Database(
    entities = [ServiceEntity::class, CheckHistoryEntity::class], version = 3, autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
    abstract fun historyDao(): CheckHistoryDao
}