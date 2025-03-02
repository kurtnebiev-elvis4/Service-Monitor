package com.mycelium.servicemonitor.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.servicemonitor.model.CheckHistoryEntity
import com.mycelium.servicemonitor.repository.HistoryRepository
import common.UIStateManager
import common.WithUIStateManger
import common.push
import common.uiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUIState(
    val loading: Boolean,
    val historyItems: List<CheckHistoryEntity>,
    val error: String
) {
    @Inject
    constructor() : this(loading = true, historyItems = emptyList(), error = "")
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository,
    override val uiStateM: UIStateManager<HistoryUIState>
) : ViewModel(), WithUIStateManger<HistoryUIState> {

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val history = repository.getAll()
                push(uiState.copy(loading = false, historyItems = history, error = ""))
            } catch (e: Exception) {
                push(uiState.copy(loading = false, error = e.message ?: "Unknown error"))
            }
        }
    }

    // Optionally add a refresh function if needed
    fun refresh() {
        loadHistory()
    }
}
