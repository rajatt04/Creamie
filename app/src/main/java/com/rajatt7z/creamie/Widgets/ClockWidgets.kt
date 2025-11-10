package com.rajatt7z.creamie.widgets.clock

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rajatt7z.creamie.utils.formatTime
import com.rajatt7z.creamie.utils.getCurrentDate
import com.rajatt7z.creamie.utils.getCurrentTime

@Composable
fun DigitalClockWidget() {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while(true) {
            kotlinx.coroutines.delay(1000)
            currentTime = getCurrentTime()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = currentTime,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = getCurrentDate(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AnalogClockWidget() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Analog Clock",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Classic Analog View", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun WorldClockWidget() {
    Column {
        ClockRow("New York", "EST", "07:45 AM")
        ClockRow("London", "GMT", "12:45 PM")
        ClockRow("Tokyo", "JST", "09:45 PM")
    }
}

@Composable
private fun ClockRow(city: String, zone: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(city, fontWeight = FontWeight.SemiBold)
            Text(zone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(time, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun StopwatchWidget() {
    var isRunning by remember { mutableStateOf(false) }
    var seconds by remember { mutableStateOf(0) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while(isRunning) {
                kotlinx.coroutines.delay(1000)
                seconds++
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            formatTime(seconds),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "Pause" else "Start")
            }
            Button(onClick = { seconds = 0; isRunning = false }) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun CountdownWidget() {
    Text(
        "⏳ Set Countdown Timer",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth()
    )
}