package com.mycelium.servicemonitor.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceForm(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    url: String,
    onUrlChange: (String) -> Unit,
    intervalText: String,
    onIntervalTextChange: (String) -> Unit,
    method: String,
    onMethodChange: (String) -> Unit,
    body: String,
    onBodyChange: (String) -> Unit,
    headersList: List<Pair<String, String>>,
    onRemoveHeader: (Pair<String, String>) -> Unit,
    onAddHeader: () -> Unit,
    saving: Boolean,
    error: String,
    isValid: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    headerDialog: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (isValid) onSave() },
                containerColor = if (isValid) MaterialTheme.colorScheme.primary else Color.Gray
            ) {
                if (saving) {
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
            if (error.isNotEmpty()) {
                Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Service Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                label = { Text("URL") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                enabled = !saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = intervalText,
                onValueChange = onIntervalTextChange,
                label = { Text("Check Interval (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            // New field for HTTP method
            OutlinedTextField(
                value = method,
                onValueChange = onMethodChange,
                label = { Text("HTTP Method") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            // New field for HTTP body
            OutlinedTextField(
                value = body,
                onValueChange = onBodyChange,
                label = { Text("HTTP Body") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving
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
                    IconButton(onClick = { onRemoveHeader(Pair(key, value)) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove header")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onAddHeader) {
                Text(text = "Add Header")
            }
        }
    }
    headerDialog()
}
