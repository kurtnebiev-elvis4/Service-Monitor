package com.mycelium.servicemonitor.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import common.CommonKeys


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceForm(
    state: ServiceFormState,
    callbacks: ServiceFormCallbacks,
    headerDialog: @Composable () -> Unit
) {

    // Local state to control dropdown expansion.
    val selectedInterval = state.intervalValue

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title) },
                navigationIcon = {
                    IconButton(onClick = { callbacks.onCancel() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.imePadding(),
                onClick = { if (state.isValid) callbacks.onSave() },
                containerColor = if (state.isValid) MaterialTheme.colorScheme.primary else Color.Gray
            ) {
                if (state.saving) {
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
            if (state.error.isNotEmpty()) {
                Text(text = "Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            OutlinedTextField(
                value = state.name,
                onValueChange = { callbacks.onNameChange(it) },
                label = { Text("Service Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.url,
                onValueChange = { callbacks.onUrlChange(it) },
                label = { Text("URL") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(autoCorrectEnabled = false),
                enabled = !state.saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Interval selection using a dropdown menu.
            var intervalDropdownExpanded by remember { mutableStateOf(false) }
            val selectedOption = CommonKeys.intervalOptionsInvert[selectedInterval]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { intervalDropdownExpanded = true }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedOption.orEmpty())
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown arrow"
                    )
                }
            }

            DropdownMenu(
                expanded = intervalDropdownExpanded,
                onDismissRequest = { intervalDropdownExpanded = false }
            ) {
                CommonKeys.intervalOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.key) },
                        onClick = {
                            callbacks.onIntervalChange(option.value)
                            intervalDropdownExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // New field for HTTP method as dropdown menu with dropdown icon.
            var httpMethodDropdownExpanded by remember { mutableStateOf(false) }
            val httpMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { httpMethodDropdownExpanded = true }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (state.method.isEmpty()) "GET" else state.method)
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown arrow"
                    )
                }
            }
            DropdownMenu(
                expanded = httpMethodDropdownExpanded,
                onDismissRequest = { httpMethodDropdownExpanded = false }
            ) {
                httpMethods.forEach { m ->
                    DropdownMenuItem(
                        text = { Text(m) },
                        onClick = {
                            callbacks.onMethodChange(m)
                            httpMethodDropdownExpanded = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // New field for HTTP body.
            OutlinedTextField(
                value = state.body,
                onValueChange = { callbacks.onBodyChange(it) },
                label = { Text("HTTP Body") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.saving
            )
            Spacer(modifier = Modifier.height(8.dp))
            // New field for SHA1 Certificate.
            OutlinedTextField(
                value = state.sha1Certificate,
                onValueChange = { callbacks.onSha1CertificateChange(it) },
                label = { Text("SHA1 Certificate") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.saving
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.responsePattern,
                onValueChange = { callbacks.onResponsePatternChange(it) },
                label = { Text("Response Pattern (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.saving,
                supportingText = { 
                    Text(
                        if (state.useRegexPattern) 
                            "Regular expression pattern to match in the response" 
                        else 
                            "Text pattern to match in the response content"
                    ) 
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Use Regular Expression",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.useRegexPattern,
                    onCheckedChange = { callbacks.onUseRegexPatternChange(it) },
                    enabled = !state.saving
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Group selection dropdown
            var groupDropdownExpanded by remember { mutableStateOf(false) }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { groupDropdownExpanded = true }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (state.groupName.isEmpty()) "No Group" else state.groupName)
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown arrow"
                    )
                }
            }
            
            DropdownMenu(
                expanded = groupDropdownExpanded,
                onDismissRequest = { groupDropdownExpanded = false }
            ) {
                // Option for no group
                DropdownMenuItem(
                    text = { Text("No Group") },
                    onClick = {
                        callbacks.onGroupNameChange("")
                        groupDropdownExpanded = false
                    }
                )
                
                // Available groups
                state.availableGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            callbacks.onGroupNameChange(group)
                            groupDropdownExpanded = false
                        }
                    )
                }
                
                // Option to create new group
                DropdownMenuItem(
                    text = { Text("Create New Group...") },
                    onClick = {
                        groupDropdownExpanded = false
                        callbacks.onCreateNewGroup()
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Headers:", style = MaterialTheme.typography.titleMedium)
            state.headersList.forEach { (key, value) ->
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
                    IconButton(onClick = { callbacks.onRemoveHeader(Pair(key, value)) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove header")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { callbacks.onAddHeader() }) {
                Text(text = "Add Header")
            }
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
    headerDialog()
}