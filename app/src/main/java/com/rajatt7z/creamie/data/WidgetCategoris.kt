package com.rajatt7z.creamie.data

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.unit.dp
import com.rajatt7z.creamie.models.WidgetCategory
import com.rajatt7z.creamie.models.WidgetItem
import com.rajatt7z.creamie.widgets.clock.DigitalClockWidget

/**
 * Returns all widget categories with their respective widgets
 */
fun getAllCategories(): List<WidgetCategory> {
    return listOf(
        WidgetCategory("Clock & Time", Icons.Default.DateRange, listOf(
            WidgetItem("Digital Clock", "Shows current time with date", "com.rajatt7z.creamie.AppWidgets.DigitalClockAppWidget") { DigitalClockWidget() }
        )),
        WidgetCategory("Media", Icons.Default.AccountBox, listOf(
            WidgetItem(
                "Wallpaper of the Day", 
                "Shows a fresh wallpaper every day", 
                "com.rajatt7z.creamie.presentation.widget.WallpaperOfTheDayWidgetReceiver"
            ) { 
                androidx.compose.material3.Text("Wallpaper Widget Preview", modifier = androidx.compose.ui.Modifier.padding(16.dp)) 
            }
        ))
    )
}