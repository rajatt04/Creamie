package com.rajatt7z.creamie.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.rajatt7z.creamie.viewmodel.WallpaperUiState
import com.rajatt7z.creamie.viewmodel.WallpaperViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperScreen(
    imageUrl: String,
    onBackClick: () -> Unit = {},
    viewModel: WallpaperViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Animated values for smooth transitions
    val animatedOpacity by animateFloatAsState(
        targetValue = uiState.imageOpacity,
        animationSpec = tween(300),
        label = "opacity"
    )
    val animatedScale by animateFloatAsState(
        targetValue = uiState.scale,
        animationSpec = tween(300),
        label = "scale"
    )
    val animatedOffsetX by animateFloatAsState(
        targetValue = uiState.offsetX,
        animationSpec = tween(300),
        label = "offsetX"
    )
    val animatedOffsetY by animateFloatAsState(
        targetValue = uiState.offsetY,
        animationSpec = tween(300),
        label = "offsetY"
    )

    // Show toast messages
    LaunchedEffect(uiState.wallpaperMessage, uiState.error) {
        uiState.wallpaperMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Customize Wallpaper",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            @Suppress("DEPRECATION")
                            (context as? Activity)?.onBackPressed()
                            onBackClick()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.resetAdjustments() },
                icon = { Icon(Icons.Default.Refresh, contentDescription = "Reset") },
                text = { Text("Reset") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image preview with adjustments
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Wallpaper Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = animatedScale,
                                scaleY = animatedScale,
                                alpha = animatedOpacity,
                                translationX = animatedOffsetX,
                                translationY = animatedOffsetY
                            )
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    viewModel.updateOffset(
                                        uiState.offsetX + dragAmount.x,
                                        uiState.offsetY + dragAmount.y
                                    )
                                }
                            },
                        contentScale = ContentScale.Crop,
                        colorFilter = if (uiState.imageTint) {
                            androidx.compose.ui.graphics.ColorFilter.tint(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        } else null
                    )

                    // Theme-based overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (uiState.imageTint) {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                        )
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                                        )
                                    )
                                }
                            )
                    )
                }
            }

            // Help text
            Text(
                text = "Drag the image to reposition • Pinch or use zoom slider to scale",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Adjustment controls
            AdjustmentControls(
                uiState = uiState,
                viewModel = viewModel
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Wallpaper setting buttons
            WallpaperButtons(
                imageUrl = imageUrl,
                isSettingWallpaper = uiState.isSettingWallpaper,
                viewModel = viewModel
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AdjustmentControls(
    uiState: WallpaperUiState,
    viewModel: WallpaperViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Adjust Image",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Opacity control
            AdjustmentSlider(
                label = "Opacity",
                value = uiState.imageOpacity,
                onValueChange = { viewModel.updateOpacity(it) },
                valueRange = 0.3f..1f
            )

            // Theme tint toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Theme Tint",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = uiState.imageTint,
                    onCheckedChange = { viewModel.toggleTint() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scale control
            AdjustmentSlider(
                label = "Zoom",
                value = uiState.scale,
                onValueChange = { viewModel.updateScale(it) },
                valueRange = 0.8f..2f
            )

            // Position controls
            AdjustmentSlider(
                label = "Position X",
                value = uiState.offsetX / 100f,
                onValueChange = { viewModel.updateOffsetX(it * 100f) },
                valueRange = -3f..3f
            )

            AdjustmentSlider(
                label = "Position Y",
                value = uiState.offsetY / 100f,
                onValueChange = { viewModel.updateOffsetY(it * 100f) },
                valueRange = -3f..3f
            )
        }
    }
}

@Composable
private fun WallpaperButtons(
    imageUrl: String,
    isSettingWallpaper: Boolean,
    viewModel: WallpaperViewModel
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Set as Wallpaper",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isSettingWallpaper) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.setWallpaper(
                                context,
                                imageUrl,
                                WallpaperManager.FLAG_SYSTEM
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = true
                    ) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("  Home", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.setWallpaper(
                                context,
                                imageUrl,
                                WallpaperManager.FLAG_LOCK
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = true
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("  Lock", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FilledTonalButton(
                    onClick = {
                        viewModel.setWallpaper(
                            context,
                            imageUrl,
                            WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true
                ) {
                    Text(
                        "Set for Both",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun AdjustmentSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Text(
                    text = String.format("%.2f", value),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun WallpaperScreenPreview() {
    MaterialTheme {
        WallpaperScreen(
            imageUrl = "https://images.pexels.com/photos/1366919/pexels-photo-1366919.jpeg"
        )
    }
}