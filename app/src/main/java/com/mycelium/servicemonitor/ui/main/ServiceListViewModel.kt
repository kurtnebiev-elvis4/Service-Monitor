package com.mycelium.servicemonitor.ui.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.servicemonitor.model.Service
import com.mycelium.servicemonitor.repository.ServiceRepository
import com.mycelium.servicemonitor.worker.ServiceCheckScheduler
import com.mycelium.servicemonitor.worker.ServiceDataExporter
import com.mycelium.servicemonitor.worker.ServiceDataImporter
import common.UIStateManager
import common.WithUIStateManger
import common.push
import common.uiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceListUIState(
    val loading: Boolean,
    val services: List<Service>,
    val error: String
) {
    @Inject
    constructor() : this(loading = true, services = emptyList(), error = "")
}

@HiltViewModel
class ServiceListViewModel @Inject constructor(
    private val repository: ServiceRepository,
    private val scheduler: ServiceCheckScheduler,
    private val exporter: ServiceDataExporter,
    private val importer: ServiceDataImporter,
    override val uiStateM: UIStateManager<ServiceListUIState>
) : ViewModel(), WithUIStateManger<ServiceListUIState> {
    init {
        viewModelScope.launch(Dispatchers.Default) {
            repository.allServicesFlow()
                .catch { e ->
                    push(
                        uiState.copy(
                            loading = false,
                            error = e.message ?: "Unknown error"
                        )
                    )
                }
                .collect { serviceList ->
                    Log.e("ServiceListViewModel", "collect list")
                    push(
                        uiState.copy(
                            loading = false,
                            services = serviceList,
                            error = ""
                        )
                    )
                }
        }
    }

    fun removeService(service: Service) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.removeService(service)
        }
    }

    fun archiveService(service: Service) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.archiveService(service)
        }
    }
    fun unarchiveService(service: Service) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.unarchiveService(service)
        }
    }

    fun checkAllNow() {
        scheduler.allServiceChecksNow()
    }

    fun exportList() {
        viewModelScope.launch(Dispatchers.Default) {
            val services = uiState.services
            exporter.exportServices(services)
        }
    }


    fun importList(uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            val services = importer.importServices(uri)
            services.forEach { service ->
                repository.insertService(service)
            }
            scheduler.scheduleAllServiceChecks()
        }
    }

    fun checkService(it: Service) {
        scheduler.checkServiceNow(it)
    }

    fun removeAll() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.removeAll()
        }
    }

    fun moveUp(service: Service) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.updateServiceOrder(service)
        }
    }
}