package com.rajatt7z.creamie.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
        ))
    )
}