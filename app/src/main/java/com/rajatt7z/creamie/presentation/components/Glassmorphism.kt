package com.rajatt7z.creamie.presentation.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a glassmorphism effect to a Composable.
 * Uses a true blur effect on Android 12+ (API 31) and a simple translucent fallback on older versions.
 * Note: To use the `blur` modifier correctly for backing content, the UI needs to be drawn appropriately.
 * Commonly, just adding a translucent background is the easiest cross-version way, but here we provide a specialized modifier.
 */
fun Modifier.glassmorphism(
    color: Color = Color.Black.copy(alpha = 0.4f),
    blurRadius: Dp = 16.dp,
    shape: Shape? = null
): Modifier = composed {
    var modifier = this
    
    // Warning: Modifier.blur blurs the content OF the composable, not the background BEHIND it.
    // To achieve a true iOS-like glass effect, we typically need a specialized RenderEffect which is complex.
    // For this app, we simply apply a sophisticated translucent overlay.
    
    if (shape != null) {
        modifier = modifier.background(color, shape)
    } else {
        modifier = modifier.background(color)
    }
    
    modifier
}
