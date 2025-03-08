package com.mycelium.servicemonitor.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mycelium.servicemonitor.ui.common.HeaderDialog
import com.mycelium.servicemonitor.ui.common.ServiceForm
import common.parseHeaders
import common.provideUIState

@Composable
fun EditServiceScreen(
    viewModel: EditServiceViewModel = hiltViewModel(),
    onServiceUpdated: () -> Unit,
    onCancel: () -> Unit
) {
    val uiState by viewModel.provideUIState().collectAsState()
    val service = uiState.service

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
    // New state for SHA-1 Certificate.
    var sha1Certificate by remember { mutableStateOf(service.sha1Certificate) }
    var headersList by remember { mutableStateOf(parseHeaders(service.headers)) }
    var showHeaderDialog by remember { mutableStateOf(false) }

    val isValid = name.isNotBlank() && url.isNotBlank()

    LaunchedEffect(uiState) {
        if (uiState.success) {
            onServiceUpdated()
        }
    }

    ServiceForm(
        title = "Edit Service",
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
        sha1Certificate = sha1Certificate,
        onSha1CertificateChange = { sha1Certificate = it },
        headersList = headersList,
        onRemoveHeader = { header -> headersList = headersList.filterNot { it == header } },
        onAddHeader = { showHeaderDialog = true },
        saving = uiState.saving,
        error = uiState.error,
        isValid = isValid,
        onSave = {
            val headersString = headersList.joinToString(",") { "${it.first}:${it.second}" }
            viewModel.updateService(
                service.copy(
                    name = name,
                    url = url,
                    interval = intervalText.toInt(),
                    headers = headersString,
                    method = method,
                    body = body,
                    sha1Certificate = sha1Certificate
                )
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