package com.rajatt7z.creamie.widgets.other

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Finance Widgets
@Composable
fun CryptoWidget() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("₿ Bitcoin", style = MaterialTheme.typography.titleMedium)
            Text("BTC/USD", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "$65,432",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text("+ 2.4%", color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
fun StockWidget() {
    Text("📈 Stock Ticker (Connect to API)", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun CurrencyWidget() {
    Column {
        Text("💱 Currency Converter", fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1 USD =")
            Text("83.25 INR", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ExpenseWidget() {
    Text("💰 Monthly Expenses: ₹12,450", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun PortfolioWidget() {
    Text("📊 Portfolio Summary (Coming Soon)", style = MaterialTheme.typography.bodyLarge)
}

// Media Widgets
@Composable
fun WallpaperWidget() {
    Text("🖼️ Wallpaper of the Day", style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun RandomImageWidget() {
    Text("🎨 Daily Random Image from Pexels", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun MusicPlayerWidget() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) { Icon(Icons.Default.KeyboardArrowLeft, "Previous") }
        IconButton(onClick = {}) { Icon(Icons.Default.PlayArrow, "Play") }
        IconButton(onClick = {}) { Icon(Icons.Default.KeyboardArrowRight, "Next") }
    }
}

@Composable
fun NowPlayingWidget() {
    Column {
        Text("🎵 Now Playing", fontWeight = FontWeight.Bold)
        Text("Song Title - Artist Name", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PhotoSlideshowWidget() {
    Text("📸 Random Photo Slideshow", style = MaterialTheme.typography.bodyLarge)
}