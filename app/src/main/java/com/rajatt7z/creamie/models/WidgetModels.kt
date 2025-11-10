package com.rajatt7z.creamie.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a category of widgets with a name, icon, and list of widgets
 */
data class WidgetCategory(
    val name: String,
    val icon: ImageVector,
    val widgets: List<WidgetItem>
)

/**
 * Represents an individual widget item
 * @param title Display name of the widget
 * @param description Short description of widget functionality
 * @param widgetClass Fully qualified class name for the actual AppWidget implementation
 * @param composable Preview composable function for UI display
 */
data class WidgetItem(
    val title: String,
    val description: String,
    val widgetClass: String? = null,
    val composable: @Composable () -> Unit
)