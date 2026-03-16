package com.rajatt7z.creamie.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rajatt7z.creamie.domain.model.Collection
import com.rajatt7z.creamie.presentation.components.AnimatedMediaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPhotoClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onCollectionClick: (String, String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val curatedPhotos = viewModel.curatedPhotos.collectAsLazyPagingItems()
    val popularVideos = viewModel.popularVideos.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
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
                        onClick = onSettingsClick,
                        shape = CircleShape, // 👈 forces circular shape
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier
                            .size(52.dp) // no padding here
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(28.dp) // control icon size directly
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 12)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // 1. Curator's Picks (Photos)
            item {
                SectionHeader(title = "Curator's Picks", subtitle = "Handpicked for you")
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(curatedPhotos.itemCount) { index ->
                        curatedPhotos[index]?.let { photo ->
                            AnimatedMediaCard(
                                thumbnailUrl = photo.src.medium,
                                aspectRatio = 0.8f,
                                title = photo.photographer,
                                isVideo = false,
                                index = index,
                                onClick = { onPhotoClick(photo.id) },
                                modifier = Modifier.width(200.dp)
                            )
                        }
                    }
                    if (curatedPhotos.loadState.append is LoadState.Loading) {
                        item {
                            Box(modifier = Modifier.width(200.dp).height(250.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            // 2. Trending Motion (Videos)
            item {
                SectionHeader(title = "Trending Motion", subtitle = "Popular videos today")
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(popularVideos.itemCount) { index ->
                        popularVideos[index]?.let { video ->
                            val durationStr = if (video.duration >= 60) {
                                "${video.duration / 60}:${(video.duration % 60).toString().padStart(2, '0')}"
                            } else {
                                "0:${video.duration.toString().padStart(2, '0')}"
                            }
                            
                            AnimatedMediaCard(
                                thumbnailUrl = video.image,
                                aspectRatio = 1.2f, // Wider for videos
                                title = video.user.name,
                                isVideo = true,
                                durationText = durationStr,
                                index = index,
                                onClick = { /* Navigate to video player */ }, // Will be wired up in NavGraph differently if needed
                                modifier = Modifier.width(260.dp)
                            )
                        }
                    }
                    if (popularVideos.loadState.append is LoadState.Loading) {
                        item {
                            Box(modifier = Modifier.width(260.dp).height(200.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            // 3. Featured Collections
            if (!uiState.isCollectionsLoading && uiState.featuredCollections.isNotEmpty()) {
                item {
                    SectionHeader(title = "Featured Collections", subtitle = "Curated groups")
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.featuredCollections) { collection ->
                            CollectionCard(
                                collection = collection,
                                onClick = { onCollectionClick(collection.id, collection.title) }
                            )
                        }
                    }
                }
            } else if (uiState.isCollectionsLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
            .clip(RoundedCornerShape(16.dp))
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