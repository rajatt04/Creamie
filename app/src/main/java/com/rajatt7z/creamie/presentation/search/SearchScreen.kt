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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
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
import com.rajatt7z.creamie.presentation.components.AnimatedPhotoCard
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
            // iOS Glassmorphism Style Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                // Glass Background
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(32.dp))
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
                ) {
                    // Optional: You could use Modifier.blur(20.dp) here if RenderEffect is supported, 
                    // but alpha + gradient simulates it well across Android versions
                }

                // Inner content
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    androidx.compose.foundation.text.BasicTextField(
                        value = uiState.query,
                        onValueChange = viewModel::onQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (uiState.query.isEmpty()) {
                                Text(
                                    text = "Search wallpapers...",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    Row {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { 
                                viewModel.onQueryChange("") 
                            }) {
                                Icon(
                                    Icons.Default.Clear, 
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = viewModel::toggleFilterSheet) {
                            Icon(
                                Icons.Default.FilterList, 
                                contentDescription = "Filters",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        
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
                    contentPadding = PaddingValues(
                        start = 12.dp, end = 12.dp, top = 12.dp, 
                        bottom = 120.dp // padding for the glassy bottom navigation bar
                    ),
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
                        count = pagingItems.itemCount
                    ) { index ->
                        pagingItems[index]?.let { photo ->
                            AnimatedPhotoCard(
                                photo = photo,
                                index = index,
                                onClick = { onPhotoClick(photo.id) }
                            )
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

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            Text(
                "Search Filters",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Orientation
            Text("Orientation", style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary))
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    FilterChip(
                        selected = orientation == null,
                        onClick = { orientation = null },
                        label = { Text("Any", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) },
                        shape = RoundedCornerShape(24.dp)
                    )
                }
                items(Constants.PEXELS_ORIENTATIONS) { opt ->
                    FilterChip(
                        selected = orientation == opt,
                        onClick = { orientation = opt },
                        label = { Text(opt.replaceFirstChar { it.uppercase() }, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) },
                        shape = RoundedCornerShape(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Size
            Text("Size", style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary))
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    FilterChip(
                        selected = size == null,
                        onClick = { size = null },
                        label = { Text("Any", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) },
                        shape = RoundedCornerShape(24.dp)
                    )
                }
                items(Constants.PEXELS_SIZES) { opt ->
                    FilterChip(
                        selected = size == opt,
                        onClick = { size = opt },
                        label = { Text(opt.replaceFirstChar { it.uppercase() }, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) },
                        shape = RoundedCornerShape(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Color
            Text("Color", style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary))
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    FilterChip(
                        selected = color == null,
                        onClick = { color = null },
                        label = { Text("Any", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) },
                        shape = RoundedCornerShape(24.dp)
                    )
                }
                items(Constants.PEXELS_COLORS) { opt ->
                    FilterChip(
                        selected = color == opt,
                        onClick = { color = opt },
                        label = { Text(opt.replaceFirstChar { it.uppercase() }, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) },
                        shape = RoundedCornerShape(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onApply(SearchFilters())
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Reset", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Button(
                    onClick = {
                        onApply(SearchFilters(orientation, size, color))
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Apply Filters", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
