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

    @Update
    suspend fun updateServices(services: List<ServiceEntity>)

    @Query("SELECT * FROM services ORDER BY position ASC")
    suspend fun getAllServices(): List<ServiceEntity>

    @Query("SELECT * FROM services  ORDER BY position ASC")
    fun allServicesFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: Int): ServiceEntity?

    @Query("DELETE FROM services")
    suspend fun deleteAllServices()

    @Query(
        """
    UPDATE services 
    SET position = CASE 
        WHEN id = :serviceId THEN :targetPosition 
        WHEN id = :swapServiceId THEN :currentPosition 
        ELSE position 
    END
    WHERE id IN (:serviceId, :swapServiceId)
"""
    )
    suspend fun swapServicePositions(
        serviceId: Int,
        targetPosition: Int,
        swapServiceId: Int,
        currentPosition: Int
    )
    @Query("SELECT * FROM services WHERE position = :position LIMIT 1")
    fun getServiceByPosition(position: Int): ServiceEntity?
}
