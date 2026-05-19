package com.rajatt7z.creamie.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rajatt7z.creamie.domain.model.Collection
import com.rajatt7z.creamie.presentation.components.AnimatedMediaCard
import com.rajatt7z.creamie.presentation.components.ShimmerPhotoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPhotoClick: (Int) -> Unit,
    onVideoClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onCollectionClick: (String, String) -> Unit,
    onSeeAllCuratedClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val curatedPhotos = viewModel.curatedPhotos.collectAsLazyPagingItems()
    val popularVideos = viewModel.popularVideos.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "DISCOVER",
                            style = MaterialTheme.typography.labelMedium.copy(
                                letterSpacing = 3.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "CREAMIE",
                            style = MaterialTheme.typography.displayLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 12)
    ) { padding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 120.dp,
                start = 16.dp,
                end = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp
        ) {
            
            // 1. Trending Motion (Videos) - Full Span
            item(span = StaggeredGridItemSpan.FullLine) {
                Column {
                    SectionHeader(
                        title = "Trending Motion", 
                        subtitle = "Popular videos today",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            count = popularVideos.itemCount,
                            key = { index -> 
                                val id = popularVideos.peek(index)?.id
                                if (id != null) "video_${id}" else "video_placeholder_$index"
                            },
                            contentType = { "video" }
                        ) { index ->
                            popularVideos[index]?.let { video ->
                                val durationStr = if (video.duration >= 60) {
                                    "${video.duration / 60}:${(video.duration % 60).toString().padStart(2, '0')}"
                                } else {
                                    "0:${video.duration.toString().padStart(2, '0')}"
                                }
                                
                                AnimatedMediaCard(
                                    thumbnailUrl = video.image,
                                    aspectRatio = 0.7f, // Taller for that "Reel/Story" aesthetic
                                    title = video.user.name,
                                    isVideo = true,
                                    durationText = durationStr,
                                    index = index,
                                    onClick = { onVideoClick(video.id) },
                                    modifier = Modifier.width(160.dp)
                                )
                            }
                        }
                        if (popularVideos.loadState.refresh is LoadState.Loading) {
                            items(5) {
                                AnimatedMediaCard(
                                    thumbnailUrl = "",
                                    aspectRatio = 0.7f,
                                    title = "",
                                    isVideo = true,
                                    index = it,
                                    onClick = {},
                                    modifier = Modifier.width(160.dp),
                                    isPlaceholder = true
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // 2. Curator's Picks (Photos) - Header Full Span
            item(span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(
                    title = "Curator's Picks", 
                    subtitle = "Handpicked for you",
                    onSeeAllClick = onSeeAllCuratedClick,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 3. Curator's Picks (Photos) - Staggered Grid Items
            items(
                count = curatedPhotos.itemCount,
                key = { index -> 
                    val id = curatedPhotos.peek(index)?.id
                    if (id != null) "photo_${id}" else "photo_placeholder_$index"
                },
                contentType = { "photo" }
            ) { index ->
                curatedPhotos[index]?.let { photo ->
                    val aspectRatio = if (photo.width > 0 && photo.height > 0) {
                        photo.width.toFloat() / photo.height.toFloat()
                    } else {
                        0.8f
                    }
                    AnimatedMediaCard(
                        thumbnailUrl = photo.src.medium,
                        aspectRatio = aspectRatio,
                        title = photo.photographer,
                        isVideo = false,
                        index = index,
                        onClick = { onPhotoClick(photo.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (curatedPhotos.loadState.refresh is LoadState.Loading) {
                items(6) { index ->
                    val placeholderRatio = if (index % 2 == 0) 0.8f else 1.2f
                    AnimatedMediaCard(
                        thumbnailUrl = "",
                        aspectRatio = placeholderRatio,
                        title = "",
                        isVideo = false,
                        index = index,
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        isPlaceholder = true
                    )
                }
            }

            if (curatedPhotos.loadState.append is LoadState.Loading) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        ShimmerPhotoCard(modifier = Modifier.width(200.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String, 
    subtitle: String, 
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onSeeAllClick != null) {
            TextButton(onClick = onSeeAllClick) {
                Text("See All")
            }
        }
    }
}

@Composable
fun CollectionCard(
    collection: Collection,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${collection.mediaCount} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}