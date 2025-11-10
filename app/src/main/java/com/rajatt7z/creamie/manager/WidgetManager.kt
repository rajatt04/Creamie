package com.rajatt7z.creamie.manager

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Handles adding widgets to the home screen
 * @param context Android context
 * @param widgetClass Fully qualified class name of the widget
 * @param widgetTitle Display name of the widget
 */
fun addWidgetToHomeScreen(context: Context, widgetClass: String?, widgetTitle: String) {
    if (widgetClass == null) {
        Toast.makeText(
            context,
            "⚠️ Widget implementation coming soon! Create ${widgetTitle}AppWidget class",
            Toast.LENGTH_LONG
        ).show()
        return
    }

    val appWidgetManager = AppWidgetManager.getInstance(context)

    try {
        // Request to pin widget (Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val myProvider = ComponentName(context, widgetClass)

            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                // Create the PendingIntent to be sent when the widget is pinned
                val successCallback = android.app.PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, Class.forName(widgetClass)),
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )

                appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
                Toast.makeText(context, "✅ Long press home screen to add $widgetTitle", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "⚠️ Widget pinning not supported on this launcher", Toast.LENGTH_SHORT).show()
            }
        } else {
            // For older Android versions, show instructions
            Toast.makeText(
                context,
                "📌 Long press home screen → Widgets → Find $widgetTitle",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "⚠️ Create ${widgetClass} class first. See implementation guide.",
            Toast.LENGTH_LONG
        ).show()
    }
}