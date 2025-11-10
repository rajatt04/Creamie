package com.rajatt7z.creamie.widgets.weather

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CurrentWeatherWidget() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("🌤️ Partly Cloudy", style = MaterialTheme.typography.titleMedium)
            Text("Ahmedabad, Gujarat", fontSize = 12.sp)
        }
        Text(
            "28°C",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ForecastWidget() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ForecastDay("Mon", "☀️", "30°")
        ForecastDay("Tue", "🌤️", "28°")
        ForecastDay("Wed", "⛈️", "25°")
    }
}

@Composable
private fun ForecastDay(day: String, icon: String, temp: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(day, fontSize = 12.sp)
        Text(icon, fontSize = 24.sp)
        Text(temp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SunriseSunsetWidget() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌅 Sunrise")
            Text("06:24 AM", fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌇 Sunset")
            Text("06:48 PM", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AQIWidget() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("🌫️ Air Quality: ", style = MaterialTheme.typography.bodyLarge)
        Text(
            "Moderate (92)",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun FeelsLikeWidget() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("🌡️ Feels Like:")
            Text("31°C", fontWeight = FontWeight.Bold)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("💧 Humidity:")
            Text("65%", fontWeight = FontWeight.Bold)
        }
    }
}