package com.rajatt7z.creamie.widgets.system

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BatteryWidget() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FavoriteBorder, "Battery", modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Battery Level", style = MaterialTheme.typography.bodyLarge)
        }
        Text("82%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StorageWidget() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("📦 Storage Used")
            Text("45.2 / 128 GB", fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = 0.35f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun RAMWidget() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("🧠 RAM Usage")
            Text("3.2 / 8 GB", fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = 0.4f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun InternetSpeedWidget() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⬇️ Download")
            Text("24.5 Mbps", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⬆️ Upload")
            Text("8.2 Mbps", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun DeviceTempWidget() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Face, "Temperature", modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text("Device Temperature", style = MaterialTheme.typography.bodyLarge)
            Text("36°C - Normal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}