package com.mycelium.servicemonitor.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mycelium.servicemonitor.ui.common.CreateNewGroupDialog
import com.mycelium.servicemonitor.ui.common.HeaderDialog
import com.mycelium.servicemonitor.ui.common.ServiceForm
import com.mycelium.servicemonitor.ui.common.ServiceFormCallbacks
import com.mycelium.servicemonitor.ui.common.ServiceFormState
import common.parseHeaders
import common.provideUIState

@Composable
fun EditServiceScreen(
    viewModel: EditServiceViewModel = hiltViewModel(),
    serviceListViewModel: ServiceListViewModel = hiltViewModel(),
    onServiceUpdated: () -> Unit,
    onCancel: () -> Unit
) {
    val uiState by viewModel.provideUIState().collectAsState()
    val service = uiState.service

    // Get available groups from ServiceListViewModel
    val serviceListUIState by serviceListViewModel.provideUIState().collectAsState()
    val availableGroups = serviceListUIState.groups

    if (service == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var name by remember { mutableStateOf(service.name) }
    var url by remember { mutableStateOf(service.url) }
    var intervalText by remember { mutableIntStateOf(service.interval) }
    var method by remember { mutableStateOf(service.method) }
    var body by remember { mutableStateOf(service.body) }
    var sha1Certificate by remember { mutableStateOf(service.sha1Certificate) }
    var groupName by remember { mutableStateOf(service.groupName) }
    var headersList by remember { mutableStateOf(parseHeaders(service.headers)) }
    var showHeaderDialog by remember { mutableStateOf(false) }
    var showGroupDialog by remember { mutableStateOf(false) }

    val isValid = name.isNotBlank() && url.isNotBlank()

    LaunchedEffect(uiState) {
        if (uiState.success) {
            onServiceUpdated()
        }
    }
    
    // Create a ServiceFormState object
    val formState = ServiceFormState(
        title = "Edit Service",
        name = name,
        url = url,
        intervalValue = intervalText,
        method = method,
        body = body,
        sha1Certificate = sha1Certificate,
        groupName = groupName,
        availableGroups = availableGroups,
        headersList = headersList,
        saving = uiState.saving,
        error = uiState.error,
        isValid = isValid
    )
    
    // Create callback implementation
    val callbacks = object : ServiceFormCallbacks {
        override fun onNameChange(value: String) { name = value }
        override fun onUrlChange(value: String) { url = value }
        override fun onIntervalChange(value: Int) { intervalText = value }
        override fun onMethodChange(value: String) { method = value }
        override fun onBodyChange(value: String) { body = value }
        override fun onSha1CertificateChange(value: String) { sha1Certificate = value }
        override fun onGroupNameChange(value: String) { groupName = value }
        override fun onAddHeader() { showHeaderDialog = true }
        override fun onRemoveHeader(header: Pair<String, String>) { 
            headersList = headersList.filterNot { it == header } 
        }
        override fun onCreateNewGroup() { showGroupDialog = true }
        override fun onSave() {
            val headersString = headersList.joinToString(",") { "${it.first}:${it.second}" }
            viewModel.updateService(
                service.copy(
                    name = name,
                    url = url,
                    interval = intervalText.toInt(),
                    headers = headersString,
                    method = method,
                    body = body,
                    sha1Certificate = sha1Certificate,
                    groupName = groupName
                )
            )
        }
        override fun onCancel() { onCancel() }
    }

    ServiceForm(
        state = formState,
        callbacks = callbacks,
        headerDialog = {
            if (showHeaderDialog) {
                HeaderDialog(
                    onDismiss = { showHeaderDialog = false },
                    onAdd = { key, value ->
                        headersList = headersList + Pair(key, value)
                        showHeaderDialog = false
                    }
                )
            }
        }
    )
    
    // Show create new group dialog if needed
    if (showGroupDialog) {
        CreateNewGroupDialog(
            onDismiss = { 
                showGroupDialog = false 
            },
            onCreateGroup = { newGroupName ->
                groupName = newGroupName
                showGroupDialog = false
            }
        )
    }
}