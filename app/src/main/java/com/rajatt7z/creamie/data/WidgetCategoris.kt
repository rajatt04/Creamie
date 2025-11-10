package com.rajatt7z.creamie.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.rajatt7z.creamie.models.WidgetCategory
import com.rajatt7z.creamie.models.WidgetItem
import com.rajatt7z.creamie.widgets.clock.*
import com.rajatt7z.creamie.widgets.weather.*
import com.rajatt7z.creamie.widgets.system.*
import com.rajatt7z.creamie.widgets.motivation.*
import com.rajatt7z.creamie.widgets.other.*
import com.rajatt7z.creamie.widgets.productivity.*

/**
 * Returns all widget categories with their respective widgets
 */
fun getAllCategories(): List<WidgetCategory> {
    return listOf(
        WidgetCategory("Clock & Time", Icons.Default.DateRange, listOf(
            WidgetItem("Digital Clock", "Shows current time with date", "com.rajatt7z.creamie.AppWidgets.DigitalClockAppWidget") { DigitalClockWidget() },
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