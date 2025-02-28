package com.mycelium.servicemonitor.repository

import com.mycelium.servicemonitor.model.Service
import com.mycelium.servicemonitor.model.toEntity
import com.mycelium.servicemonitor.model.toModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepository @Inject constructor(private val serviceDao: ServiceDao) {
    suspend fun insertService(service: Service) {
        serviceDao.insertService(service.toEntity())
    }

    suspend fun getAllServices() = serviceDao.getAllServices().map { it.toModel() }

    fun allServicesFlow() = serviceDao.allServicesFlow()
        .map { list -> list.map { it.toModel() } }


    suspend fun updateServiceStatus(service: Service) {
        serviceDao.updateService(service.toEntity())
    }

    suspend fun getServiceById(id: Int): Service? =
        serviceDao.getServiceById(id)?.toModel()

    suspend fun removeService(service: Service) =
        serviceDao.deleteService(service.toEntity())

    suspend fun archiveService(service: Service) {
        val archivedService = service.copy(archived = true)
        serviceDao.updateService(archivedService.toEntity())
    }

    suspend fun removeAll() {
        serviceDao.deleteAllServices()
    }
}

