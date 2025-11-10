package com.rajatt7z.creamie.widgets.productivity

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.rajatt7z.creamie.utils.getCurrentDate
import java.text.SimpleDateFormat
import java.util.*

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