package com.rajatt7z.creamie.Widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.rajatt7z.creamie.R
import java.text.SimpleDateFormat
import java.util.*

class DigitalClockAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id)
        }

        // ✅ Start background service safely (no foreground call)
        val serviceIntent = Intent(context, ClockUpdateService::class.java)
        context.startService(serviceIntent)
    }

    override fun onEnabled(context: Context) {
        val serviceIntent = Intent(context, ClockUpdateService::class.java)
        context.startService(serviceIntent)
    }

    override fun onDisabled(context: Context) {
        // Stop service when last widget removed
        val serviceIntent = Intent(context, ClockUpdateService::class.java)
        context.stopService(serviceIntent)
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.widget_digital_clock)

    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())

    val now = Date()
    views.setTextViewText(R.id.clock_time, timeFormat.format(now))
    views.setTextViewText(R.id.clock_date, dateFormat.format(now))

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
