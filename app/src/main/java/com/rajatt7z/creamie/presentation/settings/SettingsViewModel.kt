package com.rajatt7z.creamie.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajatt7z.creamie.data.local.datastore.ThemeMode
import com.rajatt7z.creamie.data.local.datastore.UserPreferences
import com.rajatt7z.creamie.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = settingsRepository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setDefaultQuality(quality: String) {
        viewModelScope.launch { settingsRepository.setDefaultQuality(quality) }
    }

    fun clearCache() {
        viewModelScope.launch { settingsRepository.clearCache() }
    }

    fun clearApiUsage() {
        viewModelScope.launch { settingsRepository.clearApiUsageCounters() }
    }

    fun setAutoChange(enabled: Boolean, intervalHours: Int? = null) {
        viewModelScope.launch { settingsRepository.setAutoChange(enabled, intervalHours) }
    }
}
