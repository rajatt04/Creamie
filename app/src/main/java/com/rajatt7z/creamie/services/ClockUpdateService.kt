package com.rajatt7z.creamie.services

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import com.rajatt7z.creamie.R
import com.rajatt7z.creamie.AppWidgets.DigitalClockAppWidget
import com.rajatt7z.creamie.AppWidgets.AnalogClockAppWidget
import com.rajatt7z.creamie.AppWidgets.updateAnalogAppWidget
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClockUpdateService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        startClockLoop()
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable!!)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startClockLoop() {
        runnable = object : Runnable {
            override fun run() {
                updateAllWidgets()
                handler.postDelayed(this, 1000) // every 1 second
            }
        }
        handler.post(runnable!!)
    }

    private fun updateAllWidgets() {
        val context = applicationContext
        val appWidgetManager = AppWidgetManager.getInstance(context)
        
        // Digital Clock
        val digitalComponent = ComponentName(context, DigitalClockAppWidget::class.java)
        val digitalIds = appWidgetManager.getAppWidgetIds(digitalComponent)
        
        // Analog Clock
        val analogComponent = ComponentName(context, AnalogClockAppWidget::class.java)
        val analogIds = appWidgetManager.getAppWidgetIds(analogComponent)

        if (digitalIds.isEmpty() && analogIds.isEmpty()) {
            stopSelf()
            return
        }

        if (digitalIds.isNotEmpty()) {
            val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
            val now = Date()

            for (id in digitalIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_digital_clock)
                views.setTextViewText(R.id.clock_time, timeFormat.format(now))
                views.setTextViewText(R.id.clock_date, dateFormat.format(now))
                appWidgetManager.updateAppWidget(id, views)
            }
        }
        
        if (analogIds.isNotEmpty()) {
            for (id in analogIds) {
                // We use our helper method to redraw the bitmap and update the widget
                updateAnalogAppWidget(context, appWidgetManager, id)
            }
        }
    }
}