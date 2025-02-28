package com.mycelium.servicemonitor.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mycelium.servicemonitor.model.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)

    @Query("SELECT * FROM services")
    suspend fun getAllServices(): List<ServiceEntity>

    @Query("SELECT * FROM services")
    fun allServicesFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: Int): ServiceEntity?

    @Query("DELETE FROM services")
    suspend fun deleteAllServices()
}
