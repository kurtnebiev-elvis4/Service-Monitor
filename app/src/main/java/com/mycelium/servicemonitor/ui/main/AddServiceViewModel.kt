package com.mycelium.servicemonitor.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.servicemonitor.model.Service
import com.mycelium.servicemonitor.model.ServiceEntity
import com.mycelium.servicemonitor.repository.ServiceRepository
import common.UIStateManager
import common.WithUIStateManger
import common.uiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch


data class AddServiceUIState(
    val saving: Boolean,
    val success: Boolean,
    val error: String
) {
    @Inject
    constructor() : this(saving = false, success = false, error = "")
}


@HiltViewModel
class AddServiceViewModel @Inject constructor(
    private val repository: ServiceRepository,
    override val uiStateM: UIStateManager<AddServiceUIState>
) : ViewModel(), WithUIStateManger<AddServiceUIState> {


    fun saveService(name: String, url: String, interval: Int, headers: String) {
        viewModelScope.launch {
            uiStateM.push(uiState.copy(saving = true))
            try {
                repository.insertService(
                    Service(
                        name = name,
                        url = url,
                        interval = interval,
                        headers = headers
                    )
                )
                uiStateM.push(uiState.copy(success = true))
            } catch (e: Exception) {
                uiStateM.push(uiState.copy(error = e.message ?: "Unknown error"))
            }
        }
    }
}