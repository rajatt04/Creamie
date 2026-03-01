package com.rajatt7z.creamie.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Custom bottom navigation bar with animated pill indicator and icon bounce.
 */
@Composable
fun AnimatedBottomNavBar(
    items: List<AnimatedNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier
                .height(80.dp)
                .clip(RoundedCornerShape(40.dp)),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
            tonalElevation = 16.dp,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex

                // Icon scale animation - more expressive bounce
                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.25f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "icon_scale_$index"
                )

                // Label alpha
                val labelAlpha by animateFloatAsState(
                    targetValue = if (selected) 1f else 0.5f,
                    animationSpec = tween(250),
                    label = "label_alpha_$index"
                )

                // Indicator height
                val indicatorHeight by animateFloatAsState(
                    targetValue = if (selected) 6f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "indicator_$index"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Top indicator pill
                    if (indicatorHeight > 0.1f) {
                        Canvas(
                            modifier = Modifier
                                .width(32.dp)
                                .height(indicatorHeight.dp)
                        ) {
                            drawRoundRect(
                                color = primaryColor,
                                topLeft = Offset.Zero,
                                size = Size(size.width, size.height),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(28.dp)
                            .scale(iconScale),
                        tint = if (selected) primaryColor
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = labelAlpha)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                        ),
                        color = if (selected) primaryColor
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = labelAlpha)
                    )
                }
            }
        }
    }
}

data class AnimatedNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)
