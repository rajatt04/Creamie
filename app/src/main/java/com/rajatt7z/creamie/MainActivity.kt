package com.rajatt7z.creamie

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.view.Display
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

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.rajatt7z.creamie.presentation.navigation.Routes

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPreferredRefreshRate()
        enableEdgeToEdge()
        setContent {
            val preferences by preferencesManager.preferencesFlow.collectAsState(
                initial = null
            )

            if (preferences == null) {
                return@setContent // wait for first emission avoiding initial flash and keeping the system splash screen
            }

            val isDark = when (preferences!!.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val startDestination = if (preferences!!.onboardingShown) {
                Routes.DISCOVER
            } else {
                Routes.ONBOARDING
            }

            CreamieTheme(darkTheme = isDark) {
                CreamieNavGraph(
                    startDestination = startDestination,
                    onOnboardingComplete = {
                        lifecycleScope.launch {
                            preferencesManager.setOnboardingShown(true)
                        }
                    }
                )
            }
        }
    }

    private fun setPreferredRefreshRate() {
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        val modes = display.supportedModes
        val maxMode = modes.maxByOrNull { it.refreshRate }
        if (maxMode != null) {
            val layoutParams = window.attributes
            layoutParams.preferredDisplayModeId = maxMode.modeId
            window.attributes = layoutParams
        }
    }
}