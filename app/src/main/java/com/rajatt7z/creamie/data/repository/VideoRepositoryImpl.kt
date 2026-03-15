package com.rajatt7z.creamie.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.data.mapper.toDomain
import com.rajatt7z.creamie.data.remote.PexelsApiService
import com.rajatt7z.creamie.data.remote.paging.PopularVideoPagingSource
import com.rajatt7z.creamie.data.remote.paging.SearchVideoPagingSource
import com.rajatt7z.creamie.domain.model.Video
import com.rajatt7z.creamie.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val api: PexelsApiService
) : VideoRepository {

    override fun getPopularVideos(): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { PopularVideoPagingSource(api) }
        ).flow
    }

    override fun searchVideos(
        query: String,
        orientation: String?,
        size: String?
    ): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SearchVideoPagingSource(api, query, orientation, size) }
        ).flow
    }

    override suspend fun getVideo(id: Int): NetworkResult<Video> {
        return try {
            val response = api.getVideo(id)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}
