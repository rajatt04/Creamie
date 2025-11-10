package com.rajatt7z.creamie.widgets.motivation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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