package com.mycelium.servicemonitor.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.servicemonitor.model.NotificationEntity
import com.mycelium.servicemonitor.repository.NotificationRepository
import common.UIStateManager
import common.WithUIStateManger
import common.push
import common.uiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUIState(
    val loading: Boolean,
    val notifications: List<NotificationEntity>,
    val error: String
) {
    @Inject
    constructor() : this(loading = true, notifications = emptyList(), error = "")
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    override val uiStateM: UIStateManager<NotificationsUIState>
) : ViewModel(), WithUIStateManger<NotificationsUIState> {

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch(Dispatchers.Default) {
            notificationRepository.getAllNotifications()
                .catch { e ->
                    push(uiState.copy(loading = false, error = e.message ?: "Unknown error"))
                }
                .collect { notifications ->
                    push(uiState.copy(loading = false, notifications = notifications, error = ""))
                }
        }
    }

    fun clearNotifications() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                notificationRepository.deleteAllNotifications()
                push(uiState.copy(notifications = emptyList()))
            } catch (e: Exception) {
                push(uiState.copy(error = e.message ?: "Failed to clear notifications"))
            }
        }
    }

    fun refresh() {
        loadNotifications()
    }
} 