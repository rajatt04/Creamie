package com.rajatt7z.creamie.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "creamie_preferences")

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultQuality: String = "large2x",
    val onboardingShown: Boolean = false,
    val apiRequestsUsedThisHour: Int = 0,
    val apiRateLimitPerHour: Int = 200,
    val apiRateResetTimestamp: Long = 0L,
    val totalApiRequestsThisMonth: Int = 0,
    val autoChangeEnabled: Boolean = false,
    val autoChangeIntervalHours: Int = 24 // daily default
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val DEFAULT_QUALITY = stringPreferencesKey("default_quality")
        private val ONBOARDING_SHOWN = booleanPreferencesKey("onboarding_shown")
        private val API_REQUESTS_USED_HOUR = intPreferencesKey("api_requests_used_hour")
        private val API_RATE_LIMIT_HOUR = intPreferencesKey("api_rate_limit_hour")
        private val API_RATE_RESET_TS = longPreferencesKey("api_rate_reset_timestamp")
        private val API_TOTAL_MONTH = intPreferencesKey("api_total_requests_month")
        private val AUTO_CHANGE_ENABLED = booleanPreferencesKey("auto_change_enabled")
        private val AUTO_CHANGE_INTERVAL = intPreferencesKey("auto_change_interval_hours")
    }

    val preferencesFlow: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = ThemeMode.valueOf(prefs[THEME_MODE] ?: ThemeMode.SYSTEM.name),
            defaultQuality = prefs[DEFAULT_QUALITY] ?: "large2x",
            onboardingShown = prefs[ONBOARDING_SHOWN] ?: false,
            apiRequestsUsedThisHour = prefs[API_REQUESTS_USED_HOUR] ?: 0,
            apiRateLimitPerHour = prefs[API_RATE_LIMIT_HOUR] ?: 200,
            apiRateResetTimestamp = prefs[API_RATE_RESET_TS] ?: 0L,
            totalApiRequestsThisMonth = prefs[API_TOTAL_MONTH] ?: 0,
            autoChangeEnabled = prefs[AUTO_CHANGE_ENABLED] ?: false,
            autoChangeIntervalHours = prefs[AUTO_CHANGE_INTERVAL] ?: 24
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE] = mode.name }
    }

    suspend fun setDefaultQuality(quality: String) {
        dataStore.edit { it[DEFAULT_QUALITY] = quality }
    }

    suspend fun setOnboardingShown(shown: Boolean) {
        dataStore.edit { it[ONBOARDING_SHOWN] = shown }
    }

    suspend fun updateApiUsage(
        requestsUsedThisHour: Int,
        rateLimitPerHour: Int,
        resetTimestamp: Long
    ) {
        dataStore.edit { prefs ->
            prefs[API_REQUESTS_USED_HOUR] = requestsUsedThisHour
            prefs[API_RATE_LIMIT_HOUR] = rateLimitPerHour
            prefs[API_RATE_RESET_TS] = resetTimestamp
            // Increment monthly counter
            val currentMonthly = prefs[API_TOTAL_MONTH] ?: 0
            prefs[API_TOTAL_MONTH] = currentMonthly + 1
        }
    }

    suspend fun setAutoChange(enabled: Boolean, intervalHours: Int? = null) {
        dataStore.edit { prefs ->
            prefs[AUTO_CHANGE_ENABLED] = enabled
            intervalHours?.let { prefs[AUTO_CHANGE_INTERVAL] = it }
        }
    }

    suspend fun clearApiUsageCounters() {
        dataStore.edit { prefs ->
            prefs[API_REQUESTS_USED_HOUR] = 0
            prefs[API_TOTAL_MONTH] = 0
        }
    }
}
