package com.mycelium.servicemonitor.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycelium.servicemonitor.model.ServiceEntity
import com.mycelium.servicemonitor.repository.ServiceDao

@Database(
    entities = [ServiceEntity::class], version = 1, autoMigrations = [
//        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
}