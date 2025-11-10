package com.rajatt7z.creamie.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Get current time in 12-hour format with seconds
 */
fun getCurrentTime(): String {
    return SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
}

/**
 * Get current date in MMM dd, yyyy format
 */
fun getCurrentDate(): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
}

/**
 * Format seconds into MM:SS format
 * @param seconds Total seconds to format
 * @return Formatted time string
 */
fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}