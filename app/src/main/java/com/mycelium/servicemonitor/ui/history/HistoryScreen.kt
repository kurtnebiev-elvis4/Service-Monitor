package com.mycelium.servicemonitor.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mycelium.servicemonitor.model.CheckHistoryEntity
import com.mycelium.servicemonitor.ui.main.formatTimestamp
import common.provideUIState

@Preview
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(
        HistoryUIState(
            false,
            listOf(
                CheckHistoryEntity(0, "test seervice", 0, "ok"),
                CheckHistoryEntity(0, "test seervice", 0, "error")
            ), ""
        )
    )
}

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.provideUIState().collectAsState()
    HistoryScreen(uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(uiState: HistoryUIState) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("History") })
        }
    ) { paddingValues ->
        when {
            uiState.loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.historyItems.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No history available")
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = paddingValues,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(uiState.historyItems) { historyItem ->
                        HistoryListItem(item = historyItem)
                    }
                    item { Spacer(modifier = Modifier.height(64.dp)) }
                }
            }
        }
    }
}

@Composable
fun HistoryListItem(item: CheckHistoryEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { /* Optionally handle item click */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = item.serviceName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Status: ${item.status}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Checked at: ${formatTimestamp(item.timestamp)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(16.dp)
                    .align(Alignment.CenterEnd)
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(
                        color =
                        if (item.status == "ok") Color.Green
                        else Color.Red
                    )
            )
        }
    }
}

