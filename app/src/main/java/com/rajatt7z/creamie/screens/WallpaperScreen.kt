package com.rajatt7z.creamie.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import androidx.core.graphics.scale
import androidx.compose.ui.graphics.toArgb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperScreen(
    imageUrl: String,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Image adjustment states
    var imageOpacity by remember { mutableFloatStateOf(1f) }
    var imageTint by remember { mutableStateOf(false) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isSettingWallpaper by remember { mutableStateOf(false) }

    // Animated values for smooth transitions
    val animatedOpacity by animateFloatAsState(
        targetValue = imageOpacity,
        animationSpec = tween(300),
        label = "opacity"
    )
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(300),
        label = "scale"
    )
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(300),
        label = "offsetX"
    )
    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(300),
        label = "offsetY"
    )

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
                onClick = {
                    imageOpacity = 1f
                    imageTint = false
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                },
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
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            },
                        contentScale = ContentScale.Crop,
                        colorFilter = if (imageTint) {
                            androidx.compose.ui.graphics.ColorFilter.tint(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        } else null
                    )

                    // Theme-based overlay options
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (imageTint) {
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

            // Help text for pan gesture
            Text(
                text = "Drag the image to reposition • Pinch or use zoom slider to scale",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Adjustment controls
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
                        value = imageOpacity,
                        onValueChange = { imageOpacity = it },
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
                        androidx.compose.material3.Switch(
                            checked = imageTint,
                            onCheckedChange = { imageTint = it },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scale control
                    AdjustmentSlider(
                        label = "Zoom",
                        value = scale,
                        onValueChange = { scale = it },
                        valueRange = 0.8f..2f
                    )

                    // Position controls
                    AdjustmentSlider(
                        label = "Position X",
                        value = offsetX / 100f, // Scale down for slider
                        onValueChange = { offsetX = it * 100f },
                        valueRange = -3f..3f
                    )

                    AdjustmentSlider(
                        label = "Position Y",
                        value = offsetY / 100f, // Scale down for slider
                        onValueChange = { offsetY = it * 100f },
                        valueRange = -3f..3f
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wallpaper setting buttons
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                setWallpaper(
                                    context,
                                    imageUrl,
                                    WallpaperManager.FLAG_SYSTEM,
                                    imageOpacity,
                                    imageTint,
                                    scale,
                                    offsetX,
                                    offsetY
                                )
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isSettingWallpaper
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
                                setWallpaper(
                                    context,
                                    imageUrl,
                                    WallpaperManager.FLAG_LOCK,
                                    imageOpacity,
                                    imageTint,
                                    scale,
                                    offsetX,
                                    offsetY
                                )
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isSettingWallpaper
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
                            setWallpaper(
                                context,
                                imageUrl,
                                WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK,
                                imageOpacity,
                                imageTint,
                                scale,
                                offsetX,
                                offsetY
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSettingWallpaper
                    ) {
                        Text(
                            "Set for Both",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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

fun setWallpaper(
    context: Context,
    imageUrl: String,
    flag: Int,
    opacity: Float = 1f,
    themeTint: Boolean = false,
    scale: Float = 1f,
    offsetX: Float = 0f,
    offsetY: Float = 0f
) {
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch {
        try {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Setting wallpaper...", Toast.LENGTH_SHORT).show()
            }

            // Download the image
            val originalBitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())

            // Get screen dimensions for proper wallpaper sizing
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            // Create a bitmap canvas to apply all transformations
            val processedBitmap = android.graphics.Bitmap.createBitmap(
                screenWidth,
                screenHeight,
                android.graphics.Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(processedBitmap)

            // Create paint for opacity and tint effects
            val paint = android.graphics.Paint().apply {
                alpha = (opacity * 255).toInt()
                isAntiAlias = true
            }

            // Apply theme tint if enabled
            if (themeTint) {
                // Create a simple overlay color for tinting
                val tintColor = android.graphics.Color.argb(
                    (0.3f * 255).toInt(), // 30% alpha
                    100, // Red component
                    150, // Green component
                    255  // Blue component (giving a blue-ish tint)
                )
                val colorFilter = android.graphics.PorterDuffColorFilter(
                    tintColor,
                    android.graphics.PorterDuff.Mode.OVERLAY
                )
                paint.colorFilter = colorFilter
            }

            // Calculate scaled dimensions
            val scaledWidth = (originalBitmap.width * scale).toInt()
            val scaledHeight = (originalBitmap.height * scale).toInt()

            // Scale the bitmap
            val scaledBitmap = if (scale != 1f) {
                android.graphics.Bitmap.createScaledBitmap(
                    originalBitmap,
                    scaledWidth,
                    scaledHeight,
                    true
                )
            } else {
                originalBitmap
            }

            // Calculate position with offset
            val centerX = (screenWidth - scaledWidth) / 2f + offsetX
            val centerY = (screenHeight - scaledHeight) / 2f + offsetY

            // Draw the bitmap with all transformations applied
            canvas.drawBitmap(scaledBitmap, centerX, centerY, paint)

            val wm = WallpaperManager.getInstance(context)
            wm.setBitmap(processedBitmap, null, true, flag)

            // Clean up bitmaps
            if (scaledBitmap != originalBitmap) {
                scaledBitmap.recycle()
            }
            originalBitmap.recycle()

            withContext(Dispatchers.Main) {
                val wallpaperType = when (flag) {
                    WallpaperManager.FLAG_SYSTEM -> "home screen"
                    WallpaperManager.FLAG_LOCK -> "lock screen"
                    else -> "both screens"
                }
                Toast.makeText(context, "Wallpaper set for $wallpaperType!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to set wallpaper: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun ThemeToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WSPreview() {
    MaterialTheme {
        WallpaperScreen(
            imageUrl = "https://images.pexels.com/photos/1366919/pexels-photo-1366919.jpeg"
        )
    }
}