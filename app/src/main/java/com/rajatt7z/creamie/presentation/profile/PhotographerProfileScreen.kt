package com.rajatt7z.creamie.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rajatt7z.creamie.presentation.components.AnimatedMediaCard
import com.rajatt7z.creamie.presentation.components.ShimmerPhotoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotographerProfileScreen(
    onPhotoClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: PhotographerProfileViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.portfolio.collectAsLazyPagingItems()

    // Determine a stable color for the avatar based on the name length/characters
    val avatarColor = remember(viewModel.photographerName) {
        val hue = (viewModel.photographerName.hashCode() * 137.5f) % 360f
        Color.hsv(hue, 0.6f, 0.8f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // Title is in the header below
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
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
            // Profile Header
            item(span = StaggeredGridItemSpan.FullLine) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = avatarColor
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                viewModel.photographerName.firstOrNull()?.uppercase() ?: "?",
                                color = Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        viewModel.photographerName,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Text(
                        "Portfolio Showcase",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            if (pagingItems.loadState.refresh is LoadState.Loading) {
                items(6) { ShimmerPhotoCard(modifier = Modifier.fillMaxWidth()) }
            }

            items(count = pagingItems.itemCount) { index ->
                pagingItems[index]?.let { photo ->
                    AnimatedMediaCard(
                        thumbnailUrl = photo.src.medium,
                        aspectRatio = photo.width.toFloat() / photo.height.toFloat(),
                        title = "Shot by ${photo.photographer}",
                        isVideo = false, // Added parameter
                        index = index,
                        onClick = { onPhotoClick(photo.id) }
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
                        Text("No portfolio items found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
