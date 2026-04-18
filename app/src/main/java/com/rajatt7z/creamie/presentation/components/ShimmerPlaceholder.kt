package com.rajatt7z.creamie.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value - 200, translateAnim.value - 200),
        end = Offset(translateAnim.value, translateAnim.value)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
    )
}

@Composable
fun ShimmerPhotoCard(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Adjusted height for photo card
        )
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(14.dp)
        )
    }
}

@Composable
fun ShimmerCollectionCard(
    modifier: Modifier = Modifier
) {
    ShimmerPlaceholder(
        modifier = modifier
            .width(160.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun HomeSectionSkeleton(
    itemWidth: androidx.compose.ui.unit.Dp,
    itemHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(5) {
            Column(modifier = Modifier.width(itemWidth)) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                )
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(14.dp)
                )
            }
        }
    }
}
