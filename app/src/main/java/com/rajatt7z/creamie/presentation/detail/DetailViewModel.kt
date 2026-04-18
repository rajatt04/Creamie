package com.rajatt7z.creamie.presentation.detail

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.data.repository.DownloadRepository
import com.rajatt7z.creamie.data.repository.WallpaperSetterRepository
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.repository.FavoritesRepository
import com.rajatt7z.creamie.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rajatt7z.creamie.domain.repository.FollowsRepository

data class DetailUiState(
    val photo: Photo? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val isDownloading: Boolean = false,
    val isSettingWallpaper: Boolean = false,
    val downloadProgress: Float = 0f,
    val message: String? = null,
    val selectedQuality: String = "large2x",
    val colorPalette: List<Int> = emptyList(),
    val showWallpaperDialog: Boolean = false,
    val isFollowing: Boolean = false
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository,
    private val favoritesRepository: FavoritesRepository,
    private val downloadRepository: DownloadRepository,
    private val wallpaperSetterRepository: WallpaperSetterRepository,
    private val followsRepository: FollowsRepository
) : ViewModel() {

    private val photoId: Int = savedStateHandle["photoId"] ?: 0

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadPhoto()
        observeFavorite()
    }

    private fun loadPhoto() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = photoRepository.getPhotoById(photoId)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            photo = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    observeFollowing(result.data.photographerId)
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private fun observeFavorite() {
        viewModelScope.launch {
            favoritesRepository.isFavorite(photoId).collect { isFav ->
                _uiState.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    fun toggleFavorite() {
        val photo = _uiState.value.photo ?: return
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(photo)
        }
    }

    private fun observeFollowing(photographerId: Long) {
        viewModelScope.launch {
            followsRepository.isFollowed(photographerId).collect { isFollowing ->
                _uiState.update { it.copy(isFollowing = isFollowing) }
            }
        }
    }

    fun toggleFollow() {
        val photo = _uiState.value.photo ?: return
        viewModelScope.launch {
            followsRepository.toggleFollow(
                photographerId = photo.photographerId,
                name = photo.photographer,
                url = photo.photographerUrl
            )
        }
    }

    fun setSelectedQuality(quality: String) {
        _uiState.update { it.copy(selectedQuality = quality) }
    }

    fun downloadWallpaper() {
        val photo = _uiState.value.photo ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloading = true) }
            try {
                downloadRepository.downloadWallpaper(photo, _uiState.value.selectedQuality)
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        message = "Download started!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        message = "Download failed: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun showWallpaperDialog() {
        _uiState.update { it.copy(showWallpaperDialog = true) }
    }

    fun dismissWallpaperDialog() {
        _uiState.update { it.copy(showWallpaperDialog = false) }
    }

    fun setWallpaper(flag: Int) {
        val photo = _uiState.value.photo ?: return
        val quality = _uiState.value.selectedQuality
        val url = photo.src.forQuality(quality)
        viewModelScope.launch {
            _uiState.update { it.copy(isSettingWallpaper = true, showWallpaperDialog = false) }
            wallpaperSetterRepository.setWallpaper(url, flag).fold(
                onSuccess = { msg ->
                    _uiState.update {
                        it.copy(isSettingWallpaper = false, message = msg)
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isSettingWallpaper = false,
                            message = "Failed: ${e.localizedMessage}"
                        )
                    }
                }
            )
        }
    }

    fun extractColors(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                // HARDWARE bitmaps don't support getPixels() — must copy to SOFTWARE config
                val safeBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
                    bitmap.copy(Bitmap.Config.ARGB_8888, false)
                } else {
                    bitmap
                }
                if (safeBitmap == null) return@launch

                val palette = Palette.from(safeBitmap).generate()
                val colors = listOfNotNull(
                    palette.dominantSwatch?.rgb,
                    palette.vibrantSwatch?.rgb,
                    palette.darkVibrantSwatch?.rgb,
                    palette.lightVibrantSwatch?.rgb,
                    palette.mutedSwatch?.rgb,
                    palette.darkMutedSwatch?.rgb
                )
                _uiState.update { it.copy(colorPalette = colors) }

                // Recycle the copy if we made one
                if (safeBitmap !== bitmap) safeBitmap.recycle()
            } catch (e: Exception) {
                // Silently fail — color palette is cosmetic
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
