package com.mycelium.servicemonitor.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import common.CommonKeys


@Composable
fun HeaderDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {

    var customKey by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Header") },
        text = {
            Column {
                Box {
                    var textFieldWidth by remember { mutableIntStateOf(0) }
                    val density = LocalDensity.current
                    OutlinedTextField(
                        value = customKey,
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
                        CommonKeys.predefinedKeys.forEach { key ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = { Text(text = key) },
                                onClick = {
                                    customKey = key
                                    expanded = false
                                }
                            )
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
            TextButton(onClick = { onAdd(customKey, value) }) {
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
