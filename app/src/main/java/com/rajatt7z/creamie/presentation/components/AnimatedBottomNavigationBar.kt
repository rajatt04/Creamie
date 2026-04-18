package com.rajatt7z.creamie.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rajatt7z.creamie.presentation.navigation.BottomNavItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AnimatedBottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var itemPositions by remember { mutableStateOf(List(items.size) { Offset.Zero }) }
    var itemWidths by remember { mutableStateOf(List(items.size) { 0f }) }

    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0
    val isInitialized = itemWidths.all { it > 0f }

    val indicatorHeight = 52.dp
    val indicatorWidth = 52.dp
    val indicatorWidthPx = with(LocalDensity.current) { indicatorWidth.toPx() }

    val targetOffsetX = if (isInitialized) {
        itemPositions[selectedIndex].x + (itemWidths[selectedIndex] - indicatorWidthPx) / 2f
    } else {
        0f
    }

    // Liquid/Spring animation for the floating indicator pill
    val animatedOffsetX by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = spring(
            dampingRatio = 0.6f, // Slightly bouncy
            stiffness = Spring.StiffnessMediumLow 
        ),
        label = "indicator_offset_x"
    )

    // Completely transparent background
    val barBackgroundColor = Color.Transparent
    val barBorderColor = Color.Transparent
    
    // The accent color for the highlight
    val indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(barBackgroundColor)
    ) {
        
        // The moving highlight indicator placed explicitly using an animated offset
        if (isInitialized) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                    .align(Alignment.CenterStart)
                    .size(width = indicatorWidth, height = indicatorHeight)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = currentRoute == item.route
                val coroutineScope = rememberCoroutineScope()
                
                // State to trigger the "pop" animation
                var isPressed by remember { mutableStateOf(false) }

                // 1. Scale Animation: Pops up when selected, shrinks when unselected
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.8f else if (isSelected) 1.2f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "icon_scale"
                )

                // 2. Color Animation: Smooth crossfade
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f),
                    animationSpec = tween(300),
                    label = "icon_tint"
                )

                // 3. Y-Axis Translation: Lift up slightly when selected
                val offsetY by animateFloatAsState(
                    targetValue = if (isSelected) -6f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = 300f
                    ),
                    label = "icon_offset_y"
                )

                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .fillMaxHeight()
                        .onGloballyPositioned { coordinates ->
                            val newPositions = itemPositions.toMutableList()
                            newPositions[index] = coordinates.positionInParent()
                            itemPositions = newPositions
                            
                            val newWidths = itemWidths.toMutableList()
                            newWidths[index] = coordinates.size.width.toFloat()
                            itemWidths = newWidths
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Remove default ripple
                        ) {
                            coroutineScope.launch {
                                isPressed = true
                                delay(100)
                                isPressed = false
                            }
                            onItemClick(item)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(26.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationY = offsetY
                            },
                        tint = iconTint
                    )
                }
            }
        }
    }
}
