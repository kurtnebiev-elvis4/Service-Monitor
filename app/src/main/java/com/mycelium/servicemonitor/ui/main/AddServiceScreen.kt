package com.mycelium.servicemonitor.ui.main

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mycelium.servicemonitor.ui.common.CreateNewGroupDialog
import com.mycelium.servicemonitor.ui.common.HeaderDialog
import com.mycelium.servicemonitor.ui.common.ServiceForm
import com.mycelium.servicemonitor.ui.common.ServiceFormCallbacks
import com.mycelium.servicemonitor.ui.common.ServiceFormState
import common.provideUIState

@Composable
fun AddServiceScreen(
    viewModel: AddServiceViewModel = hiltViewModel(),
    serviceListViewModel: ServiceListViewModel = hiltViewModel(),
    onServiceSaved: () -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var intervalText by remember { mutableIntStateOf(60) }
    var method by remember { mutableStateOf("GET") }
    var body by remember { mutableStateOf("") }
    var sha1Certificate by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }
    var headersList by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var showHeaderDialog by remember { mutableStateOf(false) }
    var showGroupDialog by remember { mutableStateOf(false) }

    // Get available groups from ServiceListViewModel
    val serviceListUIState by serviceListViewModel.provideUIState().collectAsState()
    val availableGroups = serviceListUIState.groups

    // Basic validation (adjust as needed)
    val isValid = name.isNotBlank() && url.isNotBlank()
    val uiState by viewModel.provideUIState().collectAsState()

    LaunchedEffect(uiState) {
        if (uiState.success) {
            onServiceSaved()
        }
    }
    
    // Create a ServiceFormState object
    val formState = ServiceFormState(
        title = "Add Service",
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
            viewModel.saveService(
                name,
                url,
                intervalText,
                headersList.joinToString(",") { "${it.first}:${it.second}" },
                method,
                body,
                sha1Certificate,
                groupName
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