package com.mycelium.servicemonitor.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.servicemonitor.model.Service
import com.mycelium.servicemonitor.repository.ServiceRepository
import common.UIStateManager
import common.WithUIStateManger
import common.push
import common.uiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditServiceUIState(
    val service: Service?,
    val saving: Boolean,
    val success: Boolean,
    val error: String
) {
    @Inject
    constructor() : this(null, saving = false, success = false, error = "")
}

@HiltViewModel
class EditServiceViewModel @Inject constructor(
    private val repository: ServiceRepository,
    savedStateHandle: SavedStateHandle,
    override val uiStateM: UIStateManager<EditServiceUIState>
) : ViewModel(), WithUIStateManger<EditServiceUIState> {

    private val serviceId: Int = savedStateHandle["serviceId"] ?: 0

    init {
        viewModelScope.launch {
            val service = repository.getServiceById(serviceId)
            push(uiState.copy(service = service, saving = false, success = false, error = ""))
        }
    }

    fun updateService(service: Service) {
        viewModelScope.launch {
            uiStateM.push(uiState.copy(saving = true))
            try {
                repository.updateServiceStatus(service)
                uiStateM.push(uiState.copy(success = true))
            } catch (e: Exception) {
                uiStateM.push(uiState.copy(error = e.message ?: "Unknown error"))
            }
        }
    }
}
