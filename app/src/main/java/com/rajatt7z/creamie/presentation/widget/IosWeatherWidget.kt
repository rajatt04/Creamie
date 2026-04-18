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

class IosWeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WeatherWidgetContent()
        }
    }

    @Composable
    private fun WeatherWidgetContent() {
        val white = Color.White
        val lightWhite = white.copy(alpha = 0.7f)
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.bg_ios_weather))
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
                            text = "My Location",
                            style = TextStyle(
                                color = ColorProvider(day = white, night = white),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "61°",
                            style = TextStyle(
                                color = ColorProvider(day = white, night = white),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    
                    // Right: Icon & High/Low
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "⛅", 
                            style = TextStyle(fontSize = 28.sp)
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "↑82°",
                            style = TextStyle(
                                color = ColorProvider(day = white, night = white),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "↓61°",
                            style = TextStyle(
                                color = ColorProvider(day = lightWhite, night = lightWhite),
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
                            color = ColorProvider(day = white, night = white),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "None for 10d",
                        style = TextStyle(
                            color = ColorProvider(day = lightWhite, night = lightWhite),
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
                            color = ColorProvider(day = white, night = white),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "52°",
                        style = TextStyle(
                            color = ColorProvider(day = lightWhite, night = lightWhite),
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
}
