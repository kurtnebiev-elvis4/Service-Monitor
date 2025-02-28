package com.mycelium.servicemonitor.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import common.provideUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceScreen(
    viewModel: AddServiceViewModel = hiltViewModel(),
    onServiceSaved: () -> Unit,
    onCancel: () -> Unit
) {
    // States for input fields
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var intervalText by remember { mutableStateOf("") }

    var headersList by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var showHeaderDialog by remember { mutableStateOf(false) }

    // Simple validation
    val isValid = name.isNotBlank() && url.isNotBlank() && intervalText.toIntOrNull() != null

    // Collect UI state from the ViewModel
    val uiState by viewModel.provideUIState().collectAsState()

    // Navigate away on success (using LaunchedEffect to react to state change)
    LaunchedEffect(uiState) {
        if (uiState.success) {
            onServiceSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Service") },
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
                        viewModel.saveService(name, url, intervalText.toInt(),
                            headersList.joinToString(",") { "${it.first}:${it.second}" })
                    }
                },
                containerColor = if (isValid) MaterialTheme.colorScheme.primary else Color.Gray
            ) {
                // Optionally show a progress indicator if saving
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
                    text = "Error: ${(uiState.error)}",
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
            // Display added headers
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
                        // Remove header if needed
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    // Predefined header keys – you can expand this list as needed.
    val predefinedKeys =
        listOf(
            "Content-Type", "Authorization", "Accept", "User-Agent", "Cache-Control",
            "x-api-key", "wallet-address", "wallet-signature"
        )

    var customKey by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // This key value will hold the final chosen key.
    val headerKey = if (customKey.isNotBlank()) customKey else ""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Header") },
        text = {
            Column {
                // OutlinedTextField for header key with dropdown support.
                Box {
                    var textFieldWidth by remember { mutableIntStateOf(0) }
                    val density = LocalDensity.current
                    OutlinedTextField(
                        value = headerKey,
                        onValueChange = { customKey = it },
                        label = { Text("Header Key") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldWidth = coordinates.size.width
                            }
                    )
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Select header key")
                    }
                    DropdownMenu(
                        modifier = Modifier.width(with(density) { textFieldWidth.toDp() }),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        predefinedKeys.forEach { key ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = { Text(text = key) },
                                onClick = {
                                    customKey = key
                                    expanded = false
                                })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Header Value") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onAdd(customKey, value)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}