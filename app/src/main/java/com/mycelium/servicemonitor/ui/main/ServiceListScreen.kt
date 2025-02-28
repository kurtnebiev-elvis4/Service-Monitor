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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.mycelium.servicemonitor.R
import com.mycelium.servicemonitor.model.Service
import common.CommonKeys
import common.provideUIState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    viewModel: ServiceListViewModel = hiltViewModel(),
    onAddServiceClick: () -> Unit,
    onEditServiceClick: (Int) -> Unit
) {
    val uiState by viewModel.provideUIState().collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.importList(it) }
        }
    )
    Scaffold(
        topBar = {
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
                            text = { Text("Remove All") },
                            onClick = {
                                menuExpanded = false
                                viewModel.removeAll()
                            }
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddServiceClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить сервис"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(uiState.services) { service ->
                ServiceListItem(service,
                    onMoveUp = { viewModel.moveUp(it) },
                    onCheck = { viewModel.checkService(it) },
                    onEdit = { onEditServiceClick(it.id) },
                    onArchive = { viewModel.archiveService(it) },
                    onRemove = { viewModel.removeService(it) })
            }
            item {
                Spacer(Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun ServiceListItem(
    service: Service,
    onMoveUp: (Service) -> Unit,
    onCheck: (Service) -> Unit,
    onEdit: (Service) -> Unit,
    onArchive: (Service) -> Unit,
    onRemove: (Service) -> Unit
) {
    // Maintain local state to toggle expanded details.
    var expanded by remember { mutableStateOf(false) }

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
                                color = if (service.status == "ok") Color.Green
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
                            text = { Text("Archive") },
                            onClick = {
                                expandedItemMenu = false
                                onArchive(service)
                            }
                        )
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
                Text(
                    text = stringResource(R.string.status, service.status),
                    style = MaterialTheme.typography.bodySmall
                )

                // Add any additional information here
            }
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