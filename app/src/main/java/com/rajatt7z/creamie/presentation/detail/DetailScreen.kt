package com.rajatt7z.creamie.presentation.detail

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rajatt7z.creamie.core.common.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    onColorSearch: (String) -> Unit,
    onPhotographerClick: (String) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    LocalUriHandler.current
    var isUiVisible by remember { mutableStateOf(true) }

    // Heart animation
    val heartScale by animateFloatAsState(
        targetValue = if (uiState.isFavorite) 1.5f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heart_scale"
    )

    // Show messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("😕", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(uiState.error ?: "Error loading photo")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) { Text("Go Back") }
            }
        }
        return
    }

    val photo = uiState.photo ?: return

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val configuration = androidx.compose.ui.platform.LocalConfiguration.current
            val screenAspect = configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.toFloat()
            val imageAspect = if (photo.height > 0) photo.width.toFloat() / photo.height.toFloat() else 1f
            val isLandscapeWider = imageAspect > screenAspect

            val horizontalScrollState = rememberScrollState()
            val verticalScrollState = rememberScrollState()

            LaunchedEffect(horizontalScrollState.maxValue, verticalScrollState.maxValue) {
                if (isLandscapeWider && horizontalScrollState.maxValue > 0) {
                    horizontalScrollState.scrollTo(horizontalScrollState.maxValue / 2)
                } else if (!isLandscapeWider && verticalScrollState.maxValue > 0) {
                    verticalScrollState.scrollTo(verticalScrollState.maxValue / 2)
                }
            }

            // Full-screen image
            AsyncImage(
                model = photo.src.large2x,
                contentDescription = photo.alt,
                modifier = Modifier
                    .then(
                        if (isLandscapeWider) Modifier.fillMaxHeight().horizontalScroll(horizontalScrollState)
                        else Modifier.fillMaxWidth().verticalScroll(verticalScrollState)
                    )
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {
                        isUiVisible = !isUiVisible
                    },
                contentScale = if (isLandscapeWider) ContentScale.FillHeight else ContentScale.FillWidth,
                onSuccess = { result ->
                    val bitmap = (result.result.drawable as? BitmapDrawable)?.bitmap
                    bitmap?.let { viewModel.extractColors(it) }
                }
            )

            // Top Action Pills
            AnimatedVisibility(
                visible = isUiVisible,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                // Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                        .size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }

                // Actions Pill
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.scale(heartScale)
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Check out this wallpaper by ${photo.photographer} on Pexels: ${photo.url}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Wallpaper"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

            // Bottom Glassmorphic Panel
            AnimatedVisibility(
                visible = isUiVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.90f))
                        .navigationBarsPadding()
                        .padding(horizontal = 32.dp, vertical = 32.dp)
                ) {
                // Photographer info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPhotographerClick(photo.photographer) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                photo.photographer.firstOrNull()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            photo.photographer,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "View Portfolio \u2192",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "• ${photo.width} x ${photo.height} px",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val isFollowing = uiState.isFollowing
                    IconButton(
                        onClick = { viewModel.toggleFollow() },
                        modifier = Modifier.size(36.dp).background(
                            color = if (isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                    ) {
                        Icon(
                            imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.PersonAdd,
                            contentDescription = if (isFollowing) "Following" else "Follow",
                            modifier = Modifier.size(20.dp),
                            tint = if (isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.background
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Scrollable attributes (Colors and Quality)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Colors
                    if (uiState.colorPalette.isNotEmpty()) {
                        item {
                            Column {
                                Text(
                                    "Extracted Colors",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    uiState.colorPalette.take(4).forEach { colorInt ->
                                        Surface(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clickable {
                                                    val hex = String.format("%06X", 0xFFFFFF and colorInt)
                                                    onColorSearch(hex)
                                                },
                                            shape = CircleShape,
                                            color = Color(colorInt),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        ) {}
                                    }
                                }
                            }
                        }
                    }

                    // Quality
                    item {
                        Column {
                            Text(
                                "Download Size",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Constants.QUALITY_OPTIONS.forEach { quality ->
                                    val selected = uiState.selectedQuality == quality
                                    Surface(
                                        modifier = Modifier.clickable { viewModel.setSelectedQuality(quality) },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    ) {
                                        Text(
                                            quality.replaceFirstChar { it.uppercase() },
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Main Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = viewModel::downloadWallpaper,
                        modifier = Modifier.weight(1f).height(64.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        enabled = !uiState.isDownloading
                    ) {
                        if (uiState.isDownloading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }

                    Button(
                        onClick = viewModel::showWallpaperDialog,
                        modifier = Modifier.weight(3f).height(64.dp),
                        shape = RoundedCornerShape(32.dp),
                        enabled = !uiState.isSettingWallpaper
                    ) {
                        if (uiState.isSettingWallpaper) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Wallpaper, contentDescription = null, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Set Wallpaper", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
        }
    }

    // Set Wallpaper dialog
    if (uiState.showWallpaperDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = viewModel::dismissWallpaperDialog
        ) {
            Surface(
                shape = RoundedCornerShape(40.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Wallpaper,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Set Wallpaper",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Where would you like to apply it?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    WallpaperOptionRow(
                        icon = Icons.Default.Home,
                        title = "Home Screen",
                        subtitle = "Main wallpaper",
                        iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                        onClick = { viewModel.setWallpaper(WallpaperManager.FLAG_SYSTEM) }
                    )

                    WallpaperOptionRow(
                        icon = Icons.Default.Lock,
                        title = "Lock Screen",
                        subtitle = "Shown when locked",
                        iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = { viewModel.setWallpaper(WallpaperManager.FLAG_LOCK) }
                    )

                    WallpaperOptionRow(
                        icon = Icons.Default.PhoneAndroid,
                        title = "Both Screens",
                        subtitle = "Home & lock screen",
                        iconBgColor = MaterialTheme.colorScheme.tertiaryContainer,
                        iconTint = MaterialTheme.colorScheme.onTertiaryContainer,
                        onClick = { viewModel.setWallpaper(WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = viewModel::dismissWallpaperDialog,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WallpaperOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconBgColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}
