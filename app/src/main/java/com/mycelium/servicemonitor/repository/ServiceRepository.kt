package com.mycelium.servicemonitor.repository

import android.util.Log
import com.mycelium.servicemonitor.model.Service
import com.mycelium.servicemonitor.model.toEntity
import com.mycelium.servicemonitor.model.toModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepository @Inject constructor(private val serviceDao: ServiceDao) {
    suspend fun insertService(service: Service) {
        // The Service model now includes sha1Certificate.
        serviceDao.insertService(service.toEntity())
    }

    suspend fun getAllServices() = serviceDao.getAllServices().map { it.toModel() }

    fun allServicesFlow() = serviceDao.allServicesFlow()
        .map { list -> list.map { it.toModel() } }

    suspend fun updateService(service: Service) {
        // Ensure the updated service includes the new sha1Certificate field.
        serviceDao.updateService(service.toEntity())
    }

    suspend fun updateServiceOrder(service: Service) {
        val swapService = serviceDao.getServiceByPosition(service.position)
        Log.e(
            "ServiceRepository",
            "${service.id} ${service.position}  ${swapService?.id ?: -1}  ${service.position}"
        )
        serviceDao.swapServicePositions(
            service.id,
            service.position,
            swapService?.id ?: -1,
            service.position + 1
        )
    }

    suspend fun getServiceById(id: Int): Service? =
        serviceDao.getServiceById(id)?.toModel()

    suspend fun removeService(service: Service) =
        serviceDao.deleteService(service.toEntity())

    suspend fun archiveService(service: Service) {
        val archivedService = service.copy(archived = true)
        serviceDao.updateService(archivedService.toEntity())
    }

    suspend fun unarchiveService(service: Service) {
        val archivedService = service.copy(archived = false)
        serviceDao.updateService(archivedService.toEntity())
    }

    suspend fun removeAll() {
        serviceDao.deleteAllServices()
    }
}