package com.rajatt7z.creamie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.rajatt7z.creamie.data.local.datastore.ThemeMode
import com.rajatt7z.creamie.data.local.datastore.UserPreferencesManager
import com.rajatt7z.creamie.presentation.navigation.CreamieNavGraph
import com.rajatt7z.creamie.ui.theme.CreamieTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences by preferencesManager.preferencesFlow.collectAsState(
                initial = com.rajatt7z.creamie.data.local.datastore.UserPreferences()
            )

            val isDark = when (preferences.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            CreamieTheme(darkTheme = isDark) {
                CreamieNavGraph()
            }
        }
    }
}