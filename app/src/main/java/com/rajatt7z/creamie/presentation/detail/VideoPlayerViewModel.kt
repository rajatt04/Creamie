package com.rajatt7z.creamie.presentation.detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.data.repository.DownloadRepository
import com.rajatt7z.creamie.domain.model.Video
import com.rajatt7z.creamie.domain.model.VideoFile
import com.rajatt7z.creamie.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoPlayerUiState(
    val video: Video? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val message: String? = null,
    val selectedQuality: VideoFile? = null,
    val showQualitySheet: Boolean = false,
    val isDownloading: Boolean = false
)

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val downloadRepository: DownloadRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val videoId = savedStateHandle.get<Int>("videoId") ?: 0

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    init {
        loadVideo()
    }

    private fun loadVideo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = videoRepository.getVideo(videoId)) {
                is NetworkResult.Success -> {
                    val videoInfo = result.data
                    // Default to highest quality available
                    val bestQuality = videoInfo.videoFiles.maxByOrNull {
                        (it.width ?: 0) * (it.height ?: 0)
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            video = videoInfo,
                            selectedQuality = bestQuality
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load video"
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun setSelectedQuality(videoFile: VideoFile) {
        _uiState.update { it.copy(selectedQuality = videoFile) }
    }

    fun setShowQualitySheet(show: Boolean) {
        _uiState.update { it.copy(showQualitySheet = show) }
    }

    fun downloadVideo() {
        val video = _uiState.value.video ?: return
        val quality = _uiState.value.selectedQuality ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDownloading = true, showQualitySheet = false) }
            try {
                downloadRepository.downloadVideo(video = video, quality = quality)
                _uiState.update { it.copy(isDownloading = false, message = "Download started") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isDownloading = false, message = "Failed to start download") }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
