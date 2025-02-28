package com.mycelium.servicemonitor.ui.main

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mycelium.servicemonitor.ui.common.HeaderDialog
import com.mycelium.servicemonitor.ui.common.ServiceForm
import common.provideUIState

@Composable
fun AddServiceScreen(
    viewModel: AddServiceViewModel = hiltViewModel(),
    onServiceSaved: () -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var intervalText by remember { mutableIntStateOf(60) }
    var method by remember { mutableStateOf("GET") }
    var body by remember { mutableStateOf("") }
    var headersList by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var showHeaderDialog by remember { mutableStateOf(false) }

    // Basic validation (adjust as needed)
    val isValid = name.isNotBlank() && url.isNotBlank()
    val uiState by viewModel.provideUIState().collectAsState()

    LaunchedEffect(uiState) {
        if (uiState.success) {
            onServiceSaved()
        }
    }

    ServiceForm(
        title = "Add Service",
        name = name,
        onNameChange = { name = it },
        url = url,
        onUrlChange = { url = it },
        intervalText = intervalText,
        onIntervalTextChange = { intervalText = it },
        method = method,
        onMethodChange = { method = it },
        body = body,
        onBodyChange = { body = it },
        headersList = headersList,
        onRemoveHeader = { header -> headersList = headersList.filterNot { it == header } },
        onAddHeader = { showHeaderDialog = true },
        saving = uiState.saving,
        error = uiState.error,
        isValid = isValid,
        onSave = {
            viewModel.saveService(
                name,
                url,
                intervalText.toInt(),
                headersList.joinToString(",") { "${it.first}:${it.second}" },
                method,
                body
            )
        },
        onCancel = onCancel,
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
}
