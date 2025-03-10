package com.mycelium.servicemonitor.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateNewGroupDialog(
    onDismiss: () -> Unit,
    onCreateGroup: (String) -> Unit
) {
    var newGroupName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Group") },
        text = {
            Column {
                Text("Enter a name for the new group:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newGroupName,
                    onValueChange = { newGroupName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Group Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (newGroupName.isNotEmpty()) {
                        onCreateGroup(newGroupName)
                    }
                },
                enabled = newGroupName.isNotEmpty()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}