package com.rajatt7z.creamie.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex

            // Icon scale animation
            val iconScale by animateFloatAsState(
                targetValue = if (selected) 1.15f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "icon_scale_$index"
            )

            // Label alpha
            val labelAlpha by animateFloatAsState(
                targetValue = if (selected) 1f else 0.6f,
                animationSpec = tween(200),
                label = "label_alpha_$index"
            )

            // Indicator height
            val indicatorHeight by animateFloatAsState(
                targetValue = if (selected) 3f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "indicator_$index"
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .then(
                        Modifier.padding(vertical = 8.dp)
                    )
                    .let { m ->
                        m.then(
                            Modifier.padding(0.dp)
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top indicator pill
                if (indicatorHeight > 0.1f) {
                    Canvas(
                        modifier = Modifier
                            .width(24.dp)
                            .height(indicatorHeight.dp)
                    ) {
                        drawRoundRect(
                            color = primaryColor,
                            topLeft = Offset.Zero,
                            size = Size(size.width, size.height),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(iconScale),
                    tint = if (selected) primaryColor
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = labelAlpha)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp
                    ),
                    color = if (selected) primaryColor
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = labelAlpha)
                )
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
