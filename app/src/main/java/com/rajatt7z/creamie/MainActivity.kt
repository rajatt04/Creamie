package com.rajatt7z.creamie

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.rajatt7z.creamie.screens.Home
import com.rajatt7z.creamie.ui.theme.CreamieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CreamieTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Check permission once when the app starts
                    LaunchedEffect(Unit) {
                        checkExactAlarmPermission(this@MainActivity)
                    }

                    Home()
                }
            }
        }
    }

    private fun checkExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (!alarmManager.canScheduleExactAlarms()) {
                // Show a toast to inform the user
                Toast.makeText(
                    context,
                    "⏰ Permission needed for real-time clock widget updates",
                    Toast.LENGTH_LONG
                ).show()

                try {
                    // Guide user to grant permission
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback if the intent fails
                    Toast.makeText(
                        context,
                        "Please enable exact alarm permission in app settings",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}