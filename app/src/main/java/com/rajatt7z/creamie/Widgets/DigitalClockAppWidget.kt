package com.yourapp.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.rajatt7z.creamie.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DigitalClockAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Widget added for first time
    }

    override fun onDisabled(context: Context) {
        // Last widget removed
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.widget_digital_clock)

    // Update time
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val currentTime = timeFormat.format(Date())
    views.setTextViewText(R.id.clock_time, currentTime)

    // Update date
    val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    views.setTextViewText(R.id.clock_date, currentDate)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}