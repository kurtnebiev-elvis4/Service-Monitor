package com.mycelium.servicemonitor.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import common.parseHeaders
import common.provideUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditServiceScreen(
    viewModel: EditServiceViewModel = hiltViewModel(),
    onServiceUpdated: () -> Unit,
    onCancel: () -> Unit
) {
    val uiState by viewModel.provideUIState().collectAsState()
    val service = uiState.service ?: return
    // Pre-populate fields with existing service values.
    var name by remember { mutableStateOf(service.name) }
    var url by remember { mutableStateOf(service.url) }
    var intervalText by remember { mutableStateOf(service.interval.toString()) }
    // Assume headers are stored as a comma‑separated "key:value" string; parse them into a list.
    var headersList by remember { mutableStateOf(parseHeaders(service.headers)) }
    var showHeaderDialog by remember { mutableStateOf(false) }

    val isValid = name.isNotBlank() && url.isNotBlank() && intervalText.toIntOrNull() != null

    LaunchedEffect(uiState) {
        if (uiState.success) {
            onServiceUpdated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Service") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isValid) {
                        // Convert headers list to JSON or any format you use.
                        val headersString =
                            headersList.joinToString(",") { "${it.first}:${it.second}" }
                        viewModel.updateService(
                            service.copy(
                                name = name,
                                url = url,
                                interval = intervalText.toInt(),
                                headers = headersString
                            )
                        )
                    }
                },
                containerColor = if (isValid) MaterialTheme.colorScheme.primary else Color.Gray
            ) {
                if (uiState.saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Filled.Check, contentDescription = "Save")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.error.isNotEmpty()) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Service Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                enabled = !uiState.saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = intervalText,
                onValueChange = { intervalText = it },
                label = { Text("Check Interval (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !uiState.saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Headers:", style = MaterialTheme.typography.titleMedium)
            headersList.forEach { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$key: $value",
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = {
                        headersList =
                            headersList.filterNot { it.first == key && it.second == value }
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove header")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showHeaderDialog = true }) {
                Text(text = "Add Header")
            }
        }
    }

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

