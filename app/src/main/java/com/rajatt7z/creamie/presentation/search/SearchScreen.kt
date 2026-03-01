package com.rajatt7z.creamie.presentation.search

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rajatt7z.creamie.core.common.Constants
import com.rajatt7z.creamie.presentation.components.ShimmerPhotoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onPhotoClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagingItems = viewModel.searchResults.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = uiState.query,
                        onQueryChange = viewModel::onQueryChange,
                        onSearch = viewModel::onSearch,
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text("Search wallpapers...") },
                        leadingIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        trailingIcon = {
                            Row {
                                if (uiState.query.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                                IconButton(onClick = viewModel::toggleFilterSheet) {
                                    Icon(Icons.Default.FilterList, contentDescription = "Filters")
                                }
                            }
                        }
                    )
                },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {}
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Show search history when query is empty
            if (uiState.query.isEmpty()) {
                SearchHistorySection(
                    history = uiState.searchHistory,
                    onItemClick = viewModel::onHistoryItemClick,
                    onDeleteItem = viewModel::onDeleteHistory,
                    onClearAll = viewModel::onClearAllHistory
                )
            } else {
                // Search results grid
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    verticalItemSpacing = 12.dp,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Loading shimmer
                    if (pagingItems.loadState.refresh is LoadState.Loading) {
                        items(6) { ShimmerPhotoCard(modifier = Modifier.fillMaxWidth()) }
                    }

                    // Results
                    items(
                        count = pagingItems.itemCount,
                        key = { pagingItems[it]?.id ?: it }
                    ) { index ->
                        pagingItems[index]?.let { photo ->
                            val aspectRatio = photo.width.toFloat() / photo.height.toFloat()
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPhotoClick(photo.id) },
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = photo.src.medium,
                                        contentDescription = photo.alt,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(aspectRatio.coerceIn(0.5f, 1.5f))
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Bottom gradient with photographer
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .align(Alignment.BottomCenter)
                                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                                            .then(
                                                Modifier.background(
                                                    Brush.verticalGradient(
                                                        listOf(Color.Transparent, Color.Black.copy(0.4f))
                                                    )
                                                )
                                            )
                                    )
                                    Text(
                                        photo.photographer,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Empty state
                    if (pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🔍", fontSize = 48.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "No results found",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "Try different keywords or filters",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Filter bottom sheet
    if (uiState.isFilterSheetVisible) {
        FilterBottomSheet(
            currentFilters = uiState.filters,
            onDismiss = viewModel::toggleFilterSheet,
            onApply = { filters ->
                viewModel.updateFilters(filters)
                viewModel.toggleFilterSheet()
            }
        )
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
                    "Search for wallpapers",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Nature, abstract, minimal, dark...",
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
                    modifier = Modifier.clickable { onItemClick(query) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    currentFilters: SearchFilters,
    onDismiss: () -> Unit,
    onApply: (SearchFilters) -> Unit
) {
    var orientation by remember { mutableStateOf(currentFilters.orientation) }
    var size by remember { mutableStateOf(currentFilters.size) }
    var color by remember { mutableStateOf(currentFilters.color) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "Search Filters",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Orientation
            Text("Orientation", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = orientation == null,
                        onClick = { orientation = null },
                        label = { Text("Any") }
                    )
                }
                items(Constants.PEXELS_ORIENTATIONS) { opt ->
                    FilterChip(
                        selected = orientation == opt,
                        onClick = { orientation = opt },
                        label = { Text(opt.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Size
            Text("Size", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = size == null,
                        onClick = { size = null },
                        label = { Text("Any") }
                    )
                }
                items(Constants.PEXELS_SIZES) { opt ->
                    FilterChip(
                        selected = size == opt,
                        onClick = { size = opt },
                        label = { Text(opt.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color
            Text("Color", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = color == null,
                        onClick = { color = null },
                        label = { Text("Any") }
                    )
                }
                items(Constants.PEXELS_COLORS) { opt ->
                    FilterChip(
                        selected = color == opt,
                        onClick = { color = opt },
                        label = { Text(opt.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onApply(SearchFilters())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = {
                        onApply(SearchFilters(orientation, size, color))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
