package com.rajatt7z.creamie.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.color.ColorProvider
import com.rajatt7z.creamie.MainActivity
import com.rajatt7z.creamie.R

import android.appwidget.AppWidgetManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition

object WeatherWidgetKeys {
    val locationName = stringPreferencesKey("location_name")
    val currentTemp = intPreferencesKey("current_temp")
    val conditionIcon = stringPreferencesKey("condition_icon")
    val maxTemp = intPreferencesKey("max_temp")
    val minTemp = intPreferencesKey("min_temp")
    val precipitationInfo = stringPreferencesKey("precipitation_info")
    val feelsLike = intPreferencesKey("feels_like")
}

class IosWeatherWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val prefs = currentState<Preferences>()
                val locName = prefs[WeatherWidgetKeys.locationName] ?: "My Location"
                val temp = prefs[WeatherWidgetKeys.currentTemp] ?: 61
                val icon = prefs[WeatherWidgetKeys.conditionIcon] ?: "⛅"
                val maxT = prefs[WeatherWidgetKeys.maxTemp] ?: 82
                val minT = prefs[WeatherWidgetKeys.minTemp] ?: 61
                val precip = prefs[WeatherWidgetKeys.precipitationInfo] ?: "None for 10d"
                val feels = prefs[WeatherWidgetKeys.feelsLike] ?: 52

                WeatherWidgetContent(locName, temp, icon, maxT, minT, precip, feels)
            }
        }
    }

    @Composable
    private fun WeatherWidgetContent(
        locationName: String,
        currentTemp: Int,
        conditionIcon: String,
        maxTemp: Int,
        minTemp: Int,
        precipitationInfo: String,
        feelsLike: Int
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer)
                .cornerRadius(24.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(16.dp)
            ) {
                // Top Section (Location/Temp and Icon/High-Low)
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left: Location & Large Temp
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = locationName,
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "$currentTemp°",
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    
                    // Right: Icon & High/Low
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = conditionIcon, 
                            style = TextStyle(fontSize = 28.sp)
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "↑$maxTemp°",
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "↓$minTemp°",
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                
                Spacer(modifier = GlanceModifier.defaultWeight())
                
                // Bottom section details
                // Precipitation
                Column {
                    Text(
                        text = "Precipitation",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = precipitationInfo,
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = 12.sp
                        )
                    )
                }
                
                Spacer(modifier = GlanceModifier.height(8.dp))
                
                // Feels Like
                Column {
                    Text(
                        text = "Feels Like",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "$feelsLike°",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

class IosWeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = IosWeatherWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        
        // Schedule periodic update
        val periodicWork = PeriodicWorkRequestBuilder<WeatherWidgetWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "weather_widget_update",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWork
        )
        
        // Trigger immediate update
        val immediateWork = OneTimeWorkRequestBuilder<WeatherWidgetWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "weather_widget_update_now",
            ExistingWorkPolicy.REPLACE,
            immediateWork
        )
    }
}
