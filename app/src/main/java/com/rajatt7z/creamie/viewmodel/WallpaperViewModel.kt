package com.rajatt7z.creamie.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajatt7z.creamie.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WallpaperUiState(
    val imageOpacity: Float = 1f,
    val imageTint: Boolean = false,
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val isSettingWallpaper: Boolean = false,
    val wallpaperMessage: String? = null,
    val error: String? = null
)

class WallpaperViewModel(
    private val repository: WallpaperRepository = WallpaperRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(WallpaperUiState())
    val uiState: StateFlow<WallpaperUiState> = _uiState.asStateFlow()

    fun updateOpacity(opacity: Float) {
        _uiState.value = _uiState.value.copy(imageOpacity = opacity)
    }

    fun toggleTint() {
        _uiState.value = _uiState.value.copy(imageTint = !_uiState.value.imageTint)
    }

    fun updateScale(scale: Float) {
        _uiState.value = _uiState.value.copy(scale = scale)
    }

    fun updateOffset(x: Float, y: Float) {
        _uiState.value = _uiState.value.copy(offsetX = x, offsetY = y)
    }

    fun updateOffsetX(x: Float) {
        _uiState.value = _uiState.value.copy(offsetX = x)
    }

    fun updateOffsetY(y: Float) {
        _uiState.value = _uiState.value.copy(offsetY = y)
    }

    fun resetAdjustments() {
        _uiState.value = WallpaperUiState()
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(wallpaperMessage = null, error = null)
    }

    fun setWallpaper(
        context: Context,
        imageUrl: String,
        flag: Int
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSettingWallpaper = true,
                wallpaperMessage = null,
                error = null
            )

            try {
                // Download the image
                val originalBitmap = repository.downloadBitmap(imageUrl)

                // Get screen dimensions
                val displayMetrics = context.resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels
                val screenHeight = displayMetrics.heightPixels

                // Process the bitmap with adjustments
                val processedBitmap = repository.processBitmap(
                    originalBitmap = originalBitmap,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    opacity = _uiState.value.imageOpacity,
                    themeTint = _uiState.value.imageTint,
                    scale = _uiState.value.scale,
                    offsetX = _uiState.value.offsetX,
                    offsetY = _uiState.value.offsetY
                )

                // Set the wallpaper
                repository.setWallpaper(context, processedBitmap, flag).fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(
                            isSettingWallpaper = false,
                            wallpaperMessage = message,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isSettingWallpaper = false,
                            error = "Failed to set wallpaper: ${exception.message}",
                            wallpaperMessage = null
                        )
                    }
                )

                // Clean up
                originalBitmap.recycle()
                processedBitmap.recycle()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSettingWallpaper = false,
                    error = "Failed to set wallpaper: ${e.message}",
                    wallpaperMessage = null
                )
            }
        }
    }
}