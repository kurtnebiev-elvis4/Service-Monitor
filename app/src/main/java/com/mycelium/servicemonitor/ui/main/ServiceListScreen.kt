package com.mycelium.servicemonitor.ui.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.annotations.Until
import com.mycelium.servicemonitor.Page
import com.mycelium.servicemonitor.R
import com.mycelium.servicemonitor.model.Service
import common.CommonKeys
import common.provideUIState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ServiceListScreen(
    viewModel: ServiceListViewModel = hiltViewModel(),
    onAddServiceClick: () -> Unit,
    onEditServiceClick: (Int) -> Unit,
    openPage: (String) -> Unit
) {
    val uiState by viewModel.provideUIState().collectAsState()
    
    // Track collapsed state for each group
    val collapsedGroups = remember { mutableStateMapOf<String, Boolean>() }
    
    Scaffold(
        topBar = {
            ListTopBar(viewModel, openPage)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddServiceClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Service"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Group active services by their groupName
            val activeServices = uiState.services.filter { !it.archived }
            val groupedServices = activeServices.groupBy { it.groupName.ifEmpty { "Ungrouped" } }
                .toSortedMap()
            
            // For each group, show group header and services
            groupedServices.forEach { (groupName, services) ->
                // Initialize collapsed state for new groups
                if (!collapsedGroups.containsKey(groupName)) {
                    collapsedGroups[groupName] = false
                }
                
                item {
                    GroupHeader(
                        groupName = groupName,
                        isCollapsed = collapsedGroups[groupName] ?: false,
                        onToggleCollapse = { collapsedGroups[groupName] = !(collapsedGroups[groupName] ?: false) }
                    )
                }
                
                // Only show services if group is not collapsed
                if (collapsedGroups[groupName] != true) {
                    items(services) { service ->
                        ServiceListItem(
                            service = service,
                            onMoveUp = { viewModel.moveUp(it) },
                            onCheck = { viewModel.checkService(it) },
                            onEdit = { onEditServiceClick(it.id) },
                            onArchive = { viewModel.archiveService(it) },
                            onUnarchive = { viewModel.unarchiveService(it) },
                            onRemove = { viewModel.removeService(it) },
                            onUpdateGroup = { service, groupName -> 
                                viewModel.updateServiceGroup(service, groupName) 
                            },
                            availableGroups = uiState.groups
                        )
                    }
                }
            }
            
            // Show archived services
            val archivedServices = uiState.services.filter { it.archived }
            if (archivedServices.isNotEmpty()) {
                // Track collapsed state for archive section
                if (!collapsedGroups.containsKey("Archive")) {
                    collapsedGroups["Archive"] = false
                }
                
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { collapsedGroups["Archive"] = !(collapsedGroups["Archive"] ?: false) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Archive",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Icon(
                            imageVector = if (collapsedGroups["Archive"] == true) 
                                Icons.Default.KeyboardArrowDown 
                            else 
                                Icons.Default.KeyboardArrowUp,
                            contentDescription = if (collapsedGroups["Archive"] == true) "Expand" else "Collapse"
                        )
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                }
                
                if (collapsedGroups["Archive"] != true) {
                    items(archivedServices) { service ->
                        ServiceListItem(
                            service = service,
                            onMoveUp = { viewModel.moveUp(it) },
                            onCheck = { viewModel.checkService(it) },
                            onEdit = { onEditServiceClick(it.id) },
                            onArchive = { viewModel.archiveService(it) },
                            onUnarchive = { viewModel.unarchiveService(it) },
                            onRemove = { viewModel.removeService(it) },
                            onUpdateGroup = { service, groupName -> 
                                viewModel.updateServiceGroup(service, groupName) 
                            },
                            availableGroups = uiState.groups
                        )
                    }
                }
            }
            
            item {
                Spacer(Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun GroupHeader(
    groupName: String,
    isCollapsed: Boolean,
    onToggleCollapse: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleCollapse() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                groupName,
                style = MaterialTheme.typography.titleLarge
            )
            Icon(
                imageVector = if (isCollapsed) 
                    Icons.Default.KeyboardArrowDown 
                else 
                    Icons.Default.KeyboardArrowUp,
                contentDescription = if (isCollapsed) "Expand" else "Collapse"
            )
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.secondary)
        )
    }
}

@Composable
fun ArchivedLabel() {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            "Archive",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colorScheme.tertiary)
        )
    }
}

@Composable
fun GroupSelectionDialog(
    currentGroup: String,
    availableGroups: List<String>,
    onGroupSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newGroup by remember { mutableStateOf("") }
    var isCreatingNew by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Group") },
        text = {
            Column {
                if (isCreatingNew) {
                    OutlinedTextField(
                        value = newGroup,
                        onValueChange = { newGroup = it },
                        label = { Text("New Group Name") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (newGroup.isNotEmpty()) {
                                    onGroupSelected(newGroup)
                                }
                            }
                        )
                    )
                } else {
                    Column {
                        Button(
                            onClick = { onGroupSelected("") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("No Group")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        availableGroups.forEach { group ->
                            Button(
                                onClick = { onGroupSelected(group) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(group)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { isCreatingNew = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create New Group")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isCreatingNew) {
                Button(
                    onClick = {
                        if (newGroup.isNotEmpty()) {
                            onGroupSelected(newGroup)
                        }
                    }
                ) {
                    Text("Save")
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
        dismissButton = {
            if (isCreatingNew) {
                Button(onClick = { isCreatingNew = false }) {
                    Text("Back")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTopBar(
    viewModel: ServiceListViewModel,
    openPage: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.importList(it) }
        }
    )
    TopAppBar(
        title = { Text("Сервисы для мониторинга") },
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }
            DropdownMenu(
                modifier = Modifier.padding(horizontal = 16.dp),
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Check All Now") },
                    onClick = {
                        menuExpanded = false
                        viewModel.checkAllNow()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Export List") },
                    onClick = {
                        menuExpanded = false
                        viewModel.exportList()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Import List") },
                    onClick = {
                        menuExpanded = false
                        importLauncher.launch(arrayOf("application/json"))
                    }
                )
                DropdownMenuItem(
                    text = { Text("History") },
                    onClick = {
                        menuExpanded = false
                        openPage(Page.HISTORY.name)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Notifications") },
                    onClick = {
                        menuExpanded = false
                        openPage(Page.NOTIFICATIONS.name)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Remove All") },
                    onClick = {
                        menuExpanded = false
                        viewModel.removeAll()
                    }
                )
            }
        })
}

@Composable
fun ServiceListItem(
    service: Service,
    onMoveUp: (Service) -> Unit,
    onCheck: (Service) -> Unit,
    onEdit: (Service) -> Unit,
    onArchive: (Service) -> Unit,
    onUnarchive: (Service) -> Unit,
    onRemove: (Service) -> Unit,
    onUpdateGroup: (Service, String) -> Unit,
    availableGroups: List<String>
) {
    // Maintain local state to toggle expanded details.
    var expanded by remember { mutableStateOf(false) }
    var showGroupSelectionDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(), // smooth expansion animation
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status circle: green for online, red for offline.
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(shape = MaterialTheme.shapes.small)
                            .background(
                                color =
                                if (service.archived) Color.Gray
                                else if (service.status == "ok") Color.Green
                                else if (service.lastChecked == 0L) Color.Gray
                                else Color.Red
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${service.position}. ${service.name}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Box {
                    var expandedItemMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { expandedItemMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Service options"
                        )
                    }
                    DropdownMenu(
                        expanded = expandedItemMenu,
                        onDismissRequest = { expandedItemMenu = false }
                    ) {
                        if (!service.archived) {
                            DropdownMenuItem(
                                text = { Text("Move up") },
                                onClick = {
                                    expandedItemMenu = false
                                    onMoveUp(service)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Check") },
                                onClick = {
                                    expandedItemMenu = false
                                    onCheck(service)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    expandedItemMenu = false
                                    onEdit(service)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Change Group") },
                                onClick = {
                                    expandedItemMenu = false
                                    showGroupSelectionDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Archive") },
                                onClick = {
                                    expandedItemMenu = false
                                    onArchive(service)
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Unarchive") },
                                onClick = {
                                    expandedItemMenu = false
                                    onUnarchive(service)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Remove") },
                            onClick = {
                                expandedItemMenu = false
                                onRemove(service)
                            }
                        )
                    }
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "URL: ${service.url}", style = MaterialTheme.typography.bodyMedium)
                if (service.groupName.isNotEmpty()) {
                    Text(text = "Group: ${service.groupName}", style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = stringResource(
                        R.string.check_interval,
                        CommonKeys.intervalOptionsInvert[service.interval].orEmpty()
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(
                        R.string.last_check,
                        formatTimestamp(service.lastChecked)
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
                if (service.status != "ok") {
                    Text(
                        text = stringResource(
                            R.string.last_succesful_check,
                            formatTimestamp(service.lastSuccessfulCheck)
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = stringResource(R.string.status, service.status),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        if (showGroupSelectionDialog) {
            GroupSelectionDialog(
                currentGroup = service.groupName,
                availableGroups = availableGroups,
                onGroupSelected = { newGroup ->
                    onUpdateGroup(service, newGroup)
                    showGroupSelectionDialog = false
                },
                onDismiss = { showGroupSelectionDialog = false }
            )
        }
    }
}

// Helper function to format the timestamp.
fun formatTimestamp(timestamp: Long): String {
    return if (timestamp > 0) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(timestamp))
    } else {
        "Никогда"
    }
}