package com.rajatt7z.creamie.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * A generalized media card supporting both photos and videos with premium animations.
 */
@Composable
fun AnimatedMediaCard(
    thumbnailUrl: String,
    aspectRatio: Float,
    title: String,
    isVideo: Boolean,
    durationText: String? = null,
    index: Int = 0,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "media_scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = (index % 10) * 80
        ),
        label = "media_alpha"
    )

    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "press_scale"
    )

    val safeAspectRatio = aspectRatio.coerceIn(0.5f, 1.8f)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale * pressScale
                scaleY = animatedScale * pressScale
                alpha = animatedAlpha
            }
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
    ) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = "Media by $title",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(safeAspectRatio),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
        )

        // Title and Video indicator
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(500, delayMillis = 200 + (index % 10) * 80)
            ) + fadeIn(tween(500, delayMillis = 200 + (index % 10) * 80)),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                if (isVideo && durationText != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Video",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = durationText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }
        }
    }
}
