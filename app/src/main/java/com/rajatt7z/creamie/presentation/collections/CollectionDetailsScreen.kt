package com.rajatt7z.creamie.presentation.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rajatt7z.creamie.presentation.components.AnimatedMediaCard
import com.rajatt7z.creamie.presentation.components.ShimmerPhotoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(
    onPhotoClick: (Int) -> Unit,
    onVideoClick: (Int) -> Unit = {},
    onBack: () -> Unit,
    viewModel: CollectionDetailsViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.media.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        viewModel.collectionTitle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp, end = 12.dp, top = padding.calculateTopPadding(), 
                bottom = 120.dp
            ),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (pagingItems.loadState.refresh is LoadState.Loading) {
                items(6) { ShimmerPhotoCard(modifier = Modifier.fillMaxWidth()) }
            }

            items(count = pagingItems.itemCount) { index ->
                pagingItems[index]?.let { photo ->
                    AnimatedMediaCard(
                        thumbnailUrl = photo.src.medium, // Using medium for grid thumbnails
                        aspectRatio = photo.width.toFloat() / photo.height.toFloat(),
                        title = photo.photographer,
                        isVideo = photo.isVideo,
                        index = index,
                        onClick = {
                            if (photo.isVideo) onVideoClick(photo.id)
                            else onPhotoClick(photo.id)
                        }
                    )
                }
            }

            if (pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No media found in this collection.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

