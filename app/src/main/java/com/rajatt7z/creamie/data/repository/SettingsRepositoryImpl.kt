package com.rajatt7z.creamie.data.repository

import android.content.Context
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.rajatt7z.creamie.data.local.CreamieDatabase
import com.rajatt7z.creamie.data.local.datastore.ThemeMode
import com.rajatt7z.creamie.data.local.datastore.UserPreferences
import com.rajatt7z.creamie.data.local.datastore.UserPreferencesManager
import com.rajatt7z.creamie.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: UserPreferencesManager,
    private val database: CreamieDatabase
) : SettingsRepository {

    override val preferences: Flow<UserPreferences> = preferencesManager.preferencesFlow

    override suspend fun setThemeMode(mode: ThemeMode) {
        preferencesManager.setThemeMode(mode)
    }

    override suspend fun setDefaultQuality(quality: String) {
        preferencesManager.setDefaultQuality(quality)
    }

    override suspend fun setOnboardingShown(shown: Boolean) {
        preferencesManager.setOnboardingShown(shown)
    }

    override suspend fun setAutoChange(enabled: Boolean, intervalHours: Int?) {
        preferencesManager.setAutoChange(enabled, intervalHours)
    }

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun clearCache() {
        // Clear Coil image cache
        context.imageLoader.diskCache?.clear()
        context.imageLoader.memoryCache?.clear()
        // Clear Room cached wallpapers and collections
        database.wallpaperDao().clearAll()
        database.collectionDao().clearAll()
    }

    override suspend fun clearApiUsageCounters() {
        preferencesManager.clearApiUsageCounters()
    }
}
