package com.rajatt7z.creamie.presentation.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rajatt7z.creamie.domain.model.Video
import com.rajatt7z.creamie.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ShortsFeedViewModel @Inject constructor(
    videoRepository: VideoRepository
) : ViewModel() {

    val popularVideos: Flow<PagingData<Video>> = videoRepository
        .getPopularVideos()
        .cachedIn(viewModelScope)
}
