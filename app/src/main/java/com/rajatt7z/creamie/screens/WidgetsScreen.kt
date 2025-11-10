package com.rajatt7z.creamie.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

// Data Models
data class WidgetCategory(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val widgets: List<WidgetItem>
)

data class WidgetItem(
    val title: String,
    val description: String,
    val widgetClass: String? = null, // Class name for actual widget implementation
    val composable: @Composable () -> Unit
)

// Widget Composables
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
fun ClockRow(city: String, zone: String, time: String) {
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

// Weather Widgets
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
fun ForecastDay(day: String, icon: String, temp: String) {
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

// System Widgets
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

// Motivation Widgets
@Composable
fun QuoteWidget() {
    Column {
        Text(
            "Stay hungry, stay foolish.",
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            "— Steve Jobs",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun RandomQuoteWidget() {
    Text(
        "💡 Tap to load random quote",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun WordOfDayWidget() {
    Column {
        Text("📖 Word of the Day", fontWeight = FontWeight.Bold)
        Text(
            "Serendipity",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text("Finding something good without looking for it", fontSize = 12.sp)
    }
}

@Composable
fun AffirmationWidget() {
    Text(
        "✨ You are capable of amazing things today!",
        style = MaterialTheme.typography.bodyLarge,
        fontStyle = FontStyle.Italic
    )
}

@Composable
fun JokeWidget() {
    Text(
        "🤣 Tap for daily joke",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

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

// Productivity Widgets
@Composable
fun CalendarWidget() {
    Column {
        Text(getCurrentDate(), fontWeight = FontWeight.Bold)
        Text("Today • ${SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())}")
    }
}

@Composable
fun TodoWidget() {
    Column {
        Text("📝 Today's Tasks", fontWeight = FontWeight.Bold)
        Text("• Complete project report")
        Text("• Team meeting at 3 PM")
        Text("• Gym workout")
    }
}

@Composable
fun NotesWidget() {
    Text("📔 Quick Add Note (Tap to write)", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun PomodoroWidget() {
    Text("🍅 Pomodoro: 25:00 (Ready to start)", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun RemindersWidget() {
    Text("🔔 2 Reminders for today", style = MaterialTheme.typography.bodyLarge)
}

// Fun Widgets
@Composable
fun MemeWidget() {
    Text("🤣 Tap to load random meme", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun MovieWidget() {
    Text("🎬 Trending: Dune Part Two", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun SongRecommendationWidget() {
    Text("🎶 Song Rec: Bohemian Rhapsody", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun FunFactWidget() {
    Text("🧠 Fun Fact: Honey never spoils!", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun MiniGameWidget() {
    Text("🎮 Mini Game: Tic Tac Toe", style = MaterialTheme.typography.bodyLarge)
}

// Helper Functions
fun getCurrentTime(): String {
    return SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
}

fun getCurrentDate(): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

// Widget Addition Function
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

// Main Widget Categories with widget class references
fun getAllCategories(): List<WidgetCategory> {
    return listOf(
        WidgetCategory("Clock & Time", Icons.Default.DateRange, listOf(
            WidgetItem("Digital Clock", "Shows current time with date", "com.rajatt7z.creamie.Widgets.DigitalClockAppWidget") { DigitalClockWidget() },
            WidgetItem("Analog Clock", "Classic analog clock view", "AnalogClockAppWidget") { AnalogClockWidget() },
            WidgetItem("World Clock", "Multiple timezone support", "WorldClockAppWidget") { WorldClockWidget() },
            WidgetItem("Stopwatch", "Interactive start/stop timer", "StopwatchAppWidget") { StopwatchWidget() },
            WidgetItem("Countdown", "Set countdown timer", "CountdownAppWidget") { CountdownWidget() }
        )),
        WidgetCategory("Weather", Icons.Default.LocationOn, listOf(
            WidgetItem("Current Weather", "Live city weather info", "CurrentWeatherAppWidget") { CurrentWeatherWidget() },
            WidgetItem("3-Day Forecast", "Weather outlook", "ForecastAppWidget") { ForecastWidget() },
            WidgetItem("Sunrise/Sunset", "Daylight information", "SunriseSunsetAppWidget") { SunriseSunsetWidget() },
            WidgetItem("Air Quality Index", "AQI data", "AQIAppWidget") { AQIWidget() },
            WidgetItem("Feels Like", "Temperature + Humidity", "FeelsLikeAppWidget") { FeelsLikeWidget() }
        )),
        WidgetCategory("System Info", Icons.Default.Settings, listOf(
            WidgetItem("Battery Status", "Current battery level", "BatteryAppWidget") { BatteryWidget() },
            WidgetItem("Storage Usage", "Device storage info", "StorageAppWidget") { StorageWidget() },
            WidgetItem("RAM Usage", "Memory consumption", "RAMAppWidget") { RAMWidget() },
            WidgetItem("Internet Speed", "Network speed test", "InternetSpeedAppWidget") { InternetSpeedWidget() },
            WidgetItem("Device Temperature", "CPU temperature", "DeviceTempAppWidget") { DeviceTempWidget() }
        )),
        WidgetCategory("Motivation", Icons.Default.Info, listOf(
            WidgetItem("Quote of the Day", "Inspirational quote", "QuoteAppWidget") { QuoteWidget() },
            WidgetItem("Random Quote", "Tap for new quote", "RandomQuoteAppWidget") { RandomQuoteWidget() },
            WidgetItem("Word of the Day", "Expand vocabulary", "WordOfDayAppWidget") { WordOfDayWidget() },
            WidgetItem("Daily Affirmation", "Positive mindset", "AffirmationAppWidget") { AffirmationWidget() },
            WidgetItem("Joke of the Day", "Daily humor", "JokeAppWidget") { JokeWidget() }
        )),
        WidgetCategory("Finance", Icons.Default.ShoppingCart, listOf(
            WidgetItem("Bitcoin Price", "Live BTC price", "CryptoAppWidget") { CryptoWidget() },
            WidgetItem("Stock Ticker", "Stock market data", "StockAppWidget") { StockWidget() },
            WidgetItem("Currency Converter", "Exchange rates", "CurrencyAppWidget") { CurrencyWidget() },
            WidgetItem("Expense Tracker", "Monthly spending", "ExpenseAppWidget") { ExpenseWidget() },
            WidgetItem("Portfolio Summary", "Investment overview", "PortfolioAppWidget") { PortfolioWidget() }
        )),
        WidgetCategory("Media", Icons.Default.AccountBox, listOf(
            WidgetItem("Wallpaper Preview", "Daily wallpaper", "WallpaperAppWidget") { WallpaperWidget() },
            WidgetItem("Random Image", "Pexels integration", "RandomImageAppWidget") { RandomImageWidget() },
            WidgetItem("Music Controls", "Player controls", "MusicPlayerAppWidget") { MusicPlayerWidget() },
            WidgetItem("Now Playing", "Current track info", "NowPlayingAppWidget") { NowPlayingWidget() },
            WidgetItem("Photo Slideshow", "Random photos", "PhotoSlideshowAppWidget") { PhotoSlideshowWidget() }
        )),
        WidgetCategory("Productivity", Icons.Default.Edit, listOf(
            WidgetItem("Calendar", "Today's date", "CalendarAppWidget") { CalendarWidget() },
            WidgetItem("To-Do List", "Task management", "TodoAppWidget") { TodoWidget() },
            WidgetItem("Quick Notes", "Fast note taking", "NotesAppWidget") { NotesWidget() },
            WidgetItem("Pomodoro Timer", "Focus sessions", "PomodoroAppWidget") { PomodoroWidget() },
            WidgetItem("Reminders", "Daily reminders", "RemindersAppWidget") { RemindersWidget() }
        )),
        WidgetCategory("Entertainment", Icons.Default.Star, listOf(
            WidgetItem("Random Meme", "Daily meme", "MemeAppWidget") { MemeWidget() },
            WidgetItem("Trending Movie", "Popular content", "MovieAppWidget") { MovieWidget() },
            WidgetItem("Song Recommendation", "Music discovery", "SongRecommendationAppWidget") { SongRecommendationWidget() },
            WidgetItem("Fun Fact", "Interesting trivia", "FunFactAppWidget") { FunFactWidget() },
            WidgetItem("Mini Game", "Quick game", "MiniGameAppWidget") { MiniGameWidget() }
        ))
    )
}

// Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetsScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Widget Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "💡 Tap any widget to add to home screen", Toast.LENGTH_LONG).show()
                    }) {
                        Icon(Icons.Default.Info, "Info")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            val categories = getAllCategories()

            // Info banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Tap any widget to add it to your home screen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            categories.forEach { category ->
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                items(category.widgets) { widget ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                addWidgetToHomeScreen(context, widget.widgetClass, widget.title)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = widget.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = widget.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                }
                                Icon(
                                    Icons.Default.AddCircle,
                                    contentDescription = "Add to home screen",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            widget.composable()
                        }
                    }
                }
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun WidgetScreenPreview() {
    MaterialTheme {
        WidgetsScreen(navController = rememberNavController())
    }
}
