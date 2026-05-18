package com.rajatt7z.creamie.presentation.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rajatt7z.creamie.domain.model.Video
import com.rajatt7z.creamie.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.rajatt7z.creamie.domain.repository.FavoritesRepository
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.model.WallpaperSrc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShortsFeedViewModel @Inject constructor(
    videoRepository: VideoRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    val popularVideos: Flow<PagingData<Video>> = videoRepository
        .getPopularVideos()
        .cachedIn(viewModelScope)

    private val _likedState = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val likedState: StateFlow<Map<Int, Boolean>> = _likedState.asStateFlow()

    fun checkIsLiked(videoId: Int) {
        viewModelScope.launch {
            favoritesRepository.isFavorite(videoId).collect { isFav ->
                _likedState.value = _likedState.value.toMutableMap().apply {
                    put(videoId, isFav)
                }
            }
        }
    }

    fun toggleLike(video: Video) {
        viewModelScope.launch {
            val photo = Photo(
                id = video.id,
                width = video.width,
                height = video.height,
                url = video.url,
                photographer = video.user.name,
                photographerUrl = video.user.url,
                photographerId = video.user.id.toLong(),
                avgColor = null,
                src = WallpaperSrc(
                    original = video.image,
                    large2x = video.image,
                    large = video.image,
                    medium = video.image,
                    small = video.image,
                    portrait = video.image,
                    landscape = video.image,
                    tiny = video.image
                ),
                liked = true,
                alt = "Video by ${video.user.name}",
                isVideo = true
            )
            favoritesRepository.toggleFavorite(photo)
        }
    }
}
