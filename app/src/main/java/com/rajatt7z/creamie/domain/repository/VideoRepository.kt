package com.rajatt7z.creamie.domain.repository

import androidx.paging.PagingData
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getPopularVideos(): Flow<PagingData<Video>>
    fun searchVideos(query: String, orientation: String? = null, size: String? = null): Flow<PagingData<Video>>
    suspend fun getVideo(id: Int): NetworkResult<Video>
}
