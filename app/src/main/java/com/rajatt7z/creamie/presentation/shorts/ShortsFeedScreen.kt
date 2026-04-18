package com.rajatt7z.creamie.presentation.shorts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@androidx.annotation.OptIn(UnstableApi::class)
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

    if (items.itemCount == 0 && items.loadState.refresh !is LoadState.Loading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text("No videos found", color = Color.White)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { items.itemCount })
    var isPlaying by remember { mutableStateOf(true) }
    var showPlayPauseIcon by remember { mutableStateOf(false) }
    var isLongPressing by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableFloatStateOf(0f) }
    var doubleTapLiked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Monitor playback progress
    LaunchedEffect(isPlaying, pagerState.currentPage) {
        if (isPlaying) {
            while (true) {
                val current = exoPlayer.currentPosition.toFloat()
                val duration = exoPlayer.duration.toFloat()
                if (duration > 0) {
                    videoProgress = current / duration
                }
                delay(200) // Slightly longer delay for smoother progress updating
            }
        }
    }

    // Auto-scroll to next page when video ends
    LaunchedEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    scope.launch {
                        val nextPagerPage = pagerState.currentPage + 1
                        if (nextPagerPage < items.itemCount) {
                            pagerState.animateScrollToPage(nextPagerPage)
                        }
                    }
                }
            }
        }
        exoPlayer.addListener(listener)
    }

    // Update ExoPlayer media item when page changes
    LaunchedEffect(pagerState.currentPage) {
        isPlaying = true // Reset play state for new video
        exoPlayer.playWhenReady = true
        val video = items[pagerState.currentPage]
        video?.let {
            // Pick a good HD/SD quality file to play
            val videoUrl = it.videoFiles.firstOrNull { file -> file.quality == "hd" }?.link
                ?: it.videoFiles.firstOrNull()?.link
                
            videoUrl?.let { url ->
                val mediaItem = MediaItem.fromUri(url.toUri())
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                isPlaying = !isPlaying
                                exoPlayer.playWhenReady = isPlaying
                                showPlayPauseIcon = true
                                scope.launch {
                                    delay(800)
                                    showPlayPauseIcon = false
                                }
                            },
                            onDoubleTap = {
                                doubleTapLiked = true
                                scope.launch {
                                    delay(1000)
                                    doubleTapLiked = false
                                }
                            },
                            onPress = {
                                tryAwaitRelease()
                                if (isLongPressing) {
                                    isLongPressing = false
                                    exoPlayer.setPlaybackSpeed(1f)
                                }
                            },
                            onLongPress = {
                                isLongPressing = true
                                exoPlayer.setPlaybackSpeed(2f)
                            }
                        )
                    }
            ) {
                // If this is the current page, show the Player, else show a thumbnail
                if (page == pagerState.currentPage) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = video.image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Centered Play/Pause Icon
                AnimatedVisibility(
                    visible = showPlayPauseIcon,
                    enter = fadeIn() + scaleIn(initialScale = 0.5f),
                    exit = fadeOut() + scaleOut(targetScale = 1.5f),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.PlayArrow, // Using PlayArrow for both as a "pulse" effect
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(80.dp)
                    )
                }

                // 2x Speed Indicator
                if (isLongPressing) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 60.dp)
                    ) {
                        Text(
                            "2x",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // Centered Like Heart
                AnimatedVisibility(
                    visible = doubleTapLiked,
                    enter = fadeIn() + scaleIn(initialScale = 0.5f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                    exit = fadeOut() + scaleOut(targetScale = 1.5f),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier.size(120.dp)
                    )
                }

                // UI Overlays (Gradient, Info, Actions)
                ShortsOverlay(
                    video = video,
                    progress = if (page == pagerState.currentPage) videoProgress else 0f,
                    durationSeconds = video.duration,
                    isDoubleTapLiked = if (page == pagerState.currentPage) doubleTapLiked else false,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun ShortsOverlay(
    video: Video,
    progress: Float,
    durationSeconds: Int,
    isDoubleTapLiked: Boolean,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }
    
    // Sync with double tap
    LaunchedEffect(isDoubleTapLiked) {
        if (isDoubleTapLiked) {
            isLiked = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 72.dp) // Exactly clearing the flush 72dp navigation bar
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.0f))
                        )
                    )
                    .padding(16.dp)
                    .padding(bottom = 16.dp) // Extra padding for progress bar
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
                                shape = CircleShape,
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
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Video • ${video.width}x${video.height} • ${video.duration}s",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Actions
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
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
                            Text("1.2k", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { /* Handle comments */ }) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.Comment,
                                    contentDescription = "Comment",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Text("45", color = Color.White, style = MaterialTheme.typography.labelSmall)
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

                        // Circular Countdown Progress
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(48.dp)
                        ) {
                            val remainingSec = (durationSeconds * (1f - progress)).roundToInt().coerceAtLeast(0)
                            
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxSize(),
                                color = Color.White,
                                strokeWidth = 2.dp,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                            
                            Text(
                                text = remainingSec.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { /* Handle more */ }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Double Tap Heart Animation (overlay on top of everything in the overlay box)
    // Actually this should probably be in the parent Box to cover the video.
    // I'll move it there in the next chunk or just implement it here for simplicity.
    // For now, let's keep it here but I'll need to trigger it from the parent.
}
