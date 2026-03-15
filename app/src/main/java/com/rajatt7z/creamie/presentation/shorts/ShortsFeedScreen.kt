package com.rajatt7z.creamie.presentation.shorts

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rajatt7z.creamie.domain.model.Video

@OptIn(UnstableApi::class)
@Composable
fun ShortsFeedScreen(
    viewModel: ShortsFeedViewModel = hiltViewModel()
) {
    val items = viewModel.popularVideos.collectAsLazyPagingItems()
    val context = LocalContext.current

    // Single ExoPlayer instance shared across the pager to save resources
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    if (items.loadState.refresh is LoadState.Loading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    if (items.itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text("No videos found", color = Color.White)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { items.itemCount })

    // Update ExoPlayer media item when page changes
    LaunchedEffect(pagerState.currentPage) {
        val video = items[pagerState.currentPage]
        video?.let {
            // Pick a good HD/SD quality file to play
            val videoUrl = it.videoFiles.firstOrNull { file -> file.quality == "hd" }?.link
                ?: it.videoFiles.firstOrNull()?.link
                
            videoUrl?.let { url ->
                val mediaItem = MediaItem.fromUri(Uri.parse(url))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) { page ->
        val video = items[page]
        if (video != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                // If this is the current page, show the Player, else show a thumbnail
                if (page == pagerState.currentPage) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false // Hide default controls (TikTok style)
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Thumbnail
                    AsyncImage(
                        model = video.image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // UI Overlays (Gradient, Info, Actions)
                ShortsOverlay(
                    video = video,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun ShortsOverlay(
    video: Video,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 120.dp) // Accounts for bottom navigation bar
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Details
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                video.user.name.firstOrNull()?.uppercase() ?: "?",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        video.user.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Video tags / info instead of description since Pexels doesn't often provide descriptions
                Text(
                    "Video • ${video.width}x${video.height} • ${video.duration}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { isLiked = !isLiked }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Red else Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text("Like", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { /* Handle share */ }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text("Share", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
