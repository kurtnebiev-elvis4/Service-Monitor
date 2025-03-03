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

    @Query("SELECT * FROM check_history ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getAll(offset: Int = 0, limit: Int = 50): List<CheckHistoryEntity>
}