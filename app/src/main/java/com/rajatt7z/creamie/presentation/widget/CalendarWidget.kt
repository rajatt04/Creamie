package com.rajatt7z.creamie.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.rajatt7z.creamie.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                CalendarWidgetContent()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun CalendarWidgetContent() {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val monthName = monthFormat.format(calendar.time)

        // Reset to first day of month to build calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1: Sun, 7: Sat
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Days of week header
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(16.dp)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Month Header
            Text(
                text = monthName,
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.padding(bottom = 8.dp)
            )

            // Days of week Header
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                daysOfWeek.forEachIndexed { index, day ->
                    val textColor = when (index) {
                        0 -> GlanceTheme.colors.error // Sunday
                        else -> GlanceTheme.colors.onSurfaceVariant
                    }
                    Box(
                        modifier = GlanceModifier.defaultWeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            style = TextStyle(
                                color = textColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Calendar Grid
            var currentDay = 1
            // A calendar has up to 6 rows
            for (row in 0..5) {
                if (currentDay > daysInMonth) break
                
                Row(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    for (col in 1..7) {
                        Box(
                            modifier = GlanceModifier.defaultWeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (row == 0 && col < firstDayOfWeek) {
                                // Empty slot before 1st day
                                Spacer(modifier = GlanceModifier.size(16.dp))
                            } else if (currentDay <= daysInMonth) {
                                val isToday = currentDay == today
                                val isSunday = col == 1
                                
                                val bgColor = if (isToday) GlanceTheme.colors.primary else ColorProvider(Color.Transparent)
                                val textColor = if (isToday) GlanceTheme.colors.onPrimary
                                    else if (isSunday) GlanceTheme.colors.error
                                    else GlanceTheme.colors.onSurface

                                Box(
                                    modifier = GlanceModifier
                                        .size(20.dp)
                                        .background(bgColor)
                                        .cornerRadius(10.dp), // Circular
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentDay.toString(),
                                        style = TextStyle(
                                            color = textColor,
                                            fontSize = 12.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                                currentDay++
                            } else {
                                // Empty slot after last day
                                Spacer(modifier = GlanceModifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()
}
