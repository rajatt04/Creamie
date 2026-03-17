package com.rajatt7z.creamie.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchScreen(
    onSearchPhotos: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(32.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                        .blur(16.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    androidx.compose.foundation.text.BasicTextField(
                        value = currentQuery,
                        onValueChange = { currentQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (currentQuery.isEmpty()) {
                                Text(
                                    text = "Search images...",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            
            if (currentQuery.isNotEmpty()) {
                // Action buttons to search in Photos vs Videos
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("Search in", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            viewModel.onSearch(currentQuery)
                            onSearchPhotos(android.net.Uri.encode(currentQuery))
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Photos for \"$currentQuery\"", style = MaterialTheme.typography.titleMedium)
                    }
                    

                }
            } else {
                // History
                SearchHistorySection(
                    history = uiState.searchHistory,
                    onItemClick = { query -> 
                        currentQuery = query 
                    },
                    onDeleteItem = viewModel::onDeleteHistory,
                    onClearAll = viewModel::onClearAllHistory
                )
            }
        }
    }
}

@Composable
private fun SearchHistorySection(
    history: List<String>,
    onItemClick: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit
) {
    if (history.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔍", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "What are you looking for?",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Try nature, minimal, coding...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Searches",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = onClearAll) {
                        Text("Clear All")
                    }
                }
            }
            items(history) { query ->
                ListItem(
                    headlineContent = { Text(query) },
                    leadingContent = { Icon(Icons.Default.History, contentDescription = null) },
                    trailingContent = {
                        IconButton(onClick = { onDeleteItem(query) }) {
                            Icon(Icons.Default.Close, contentDescription = "Delete")
                        }
                    },
                    modifier = Modifier.clickable { onItemClick(query) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}
