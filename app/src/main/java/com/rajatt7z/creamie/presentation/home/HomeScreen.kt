package com.rajatt7z.creamie.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rajatt7z.creamie.presentation.components.AnimatedPhotoCard
import com.rajatt7z.creamie.presentation.components.ShimmerPhotoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPhotoClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onCollectionClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.curatedPhotos.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(bottom = 0.dp)) {
                        Text(
                            "DISCOVER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 3.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "CREAMIE",
                            style = MaterialTheme.typography.displayMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                },
                actions = {
                    FilledIconButton(
                        onClick = onSearchClick,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(22.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 12)
    ) { padding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp, end = 12.dp,
                top = padding.calculateTopPadding() + 8.dp,
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

            // Photo grid
            items(
                count = pagingItems.itemCount
                // Explicitly omitted `key` parameter. 
                // Jetpack Compose will automatically use the position as a safe fallback key 
                // which resolves the 'already used key' crash caused by duplicate placeholders.
            ) { index ->
                pagingItems[index]?.let { photo ->
                    AnimatedPhotoCard(
                        photo = photo,
                        index = index,
                        onClick = { onPhotoClick(photo.id) }
                    )
                }
            }

            // Append loading
            if (pagingItems.loadState.append is LoadState.Loading) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
            }

            // Error state
            if (pagingItems.loadState.refresh is LoadState.Error) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    ErrorCard(
                        message = (pagingItems.loadState.refresh as LoadState.Error).error.localizedMessage
                            ?: "Something went wrong",
                        onRetry = { pagingItems.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "😕",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}