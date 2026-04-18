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

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onSearchPhotos: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentQuery by remember { mutableStateOf("") }
    val suggestedQueries = remember { 
        listOf("Nature", "Minimal", "Coding", "Abstract", "Aesthetic", "Dark", "Neon", "City", "Cars", "Space", "Animals", "Architecture", "Technology", "Fashion", "Travel") 
    }

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
                val matchingSuggestions = remember(currentQuery, uiState.searchHistory) {
                    val combined = (uiState.searchHistory + suggestedQueries).distinct()
                    combined.filter { it.contains(currentQuery, ignoreCase = true) && !it.equals(currentQuery, ignoreCase = true) }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                            Text("Search for", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { 
                                    viewModel.onSearch(currentQuery)
                                    onSearchPhotos(android.net.Uri.encode(currentQuery))
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("\"$currentQuery\"", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }

                    if (matchingSuggestions.isNotEmpty()) {
                        item {
                            Text(
                                "Suggestions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                        items(matchingSuggestions) { suggestion ->
                            ListItem(
                                headlineContent = { Text(suggestion, fontWeight = FontWeight.Medium) },
                                leadingContent = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        currentQuery = suggestion
                                        viewModel.onSearch(suggestion)
                                        onSearchPhotos(android.net.Uri.encode(suggestion))
                                    },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
            } else {
                // History
                SearchHistorySection(
                    history = uiState.searchHistory,
                    suggestions = suggestedQueries,
                    onItemClick = { query -> 
                        currentQuery = query 
                        viewModel.onSearch(query)
                        onSearchPhotos(android.net.Uri.encode(query))
                    },
                    onDeleteItem = viewModel::onDeleteHistory,
                    onClearAll = viewModel::onClearAllHistory
                )
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun SearchHistorySection(
    history: List<String>,
    suggestions: List<String>,
    onItemClick: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp, start = 16.dp, end = 16.dp)
    ) {
        if (history.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Searches",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    TextButton(onClick = onClearAll) {
                        Text("Clear All", fontWeight = FontWeight.Medium)
                    }
                }
            }
            items(history) { query ->
                ListItem(
                    headlineContent = { Text(query, fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingContent = {
                        IconButton(onClick = { onDeleteItem(query) }) {
                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onItemClick(query) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        } else {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(28.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "What are you looking for?",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Try searching for some ideas below",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                Text(
                    "Suggested Searches",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    suggestions.forEach { suggestion ->
                        Surface(
                            onClick = { onItemClick(suggestion) },
                            shape = RoundedCornerShape(100),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.clip(RoundedCornerShape(100))
                        ) {
                            Text(
                                text = suggestion,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
