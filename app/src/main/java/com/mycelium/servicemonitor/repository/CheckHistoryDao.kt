package com.mycelium.servicemonitor.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mycelium.servicemonitor.model.CheckHistoryEntity

@Dao
interface CheckHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CheckHistoryEntity)

    @Query("SELECT * FROM check_history")
    suspend fun getAll(): List<CheckHistoryEntity>
}