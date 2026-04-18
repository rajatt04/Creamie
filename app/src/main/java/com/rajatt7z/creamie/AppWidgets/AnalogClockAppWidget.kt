package com.rajatt7z.creamie.AppWidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.rajatt7z.creamie.MainActivity
import com.rajatt7z.creamie.R

/**
 * Implementation of App Widget functionality.
 * This clock is updated every second by ClockUpdateService.
 */
class AnalogClockAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAnalogAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Start the clock update service if not running
        val intent = Intent(context, com.rajatt7z.creamie.services.ClockUpdateService::class.java)
        context.startService(intent)
    }

    override fun onDisabled(context: Context) {
        // Stop service if no more widgets? (handled inside service if both digital and analog are 0)
    }
}

internal fun updateAnalogAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.widget_analog_clock)
    
    // Set the initial image
    val bitmap = AnalogClockDrawer.drawAnalogClock(context)
    views.setImageViewBitmap(R.id.analog_clock_image, bitmap)

    // Setup click intent to open main activity
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        appWidgetId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.analog_clock_image, pendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
