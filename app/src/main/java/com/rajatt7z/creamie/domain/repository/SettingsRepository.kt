package com.rajatt7z.creamie.domain.repository

import com.rajatt7z.creamie.data.local.datastore.ThemeMode
import com.rajatt7z.creamie.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val preferences: Flow<UserPreferences>

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun setDefaultQuality(quality: String)

    suspend fun setOnboardingShown(shown: Boolean)

    suspend fun setAutoChange(enabled: Boolean, intervalHours: Int? = null)

    suspend fun clearCache()

    suspend fun clearApiUsageCounters()
}
