package com.rajatt7z.creamie.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.data.local.CreamieDatabase
import com.rajatt7z.creamie.data.local.mediator.PopularVideoRemoteMediator
import com.rajatt7z.creamie.data.mapper.toDomain
import com.rajatt7z.creamie.data.remote.PexelsApiService
import com.rajatt7z.creamie.domain.model.Video
import com.rajatt7z.creamie.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val api: PexelsApiService,
    private val db: CreamieDatabase
) : VideoRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPopularVideos(): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = PopularVideoRemoteMediator(api, db),
            pagingSourceFactory = { db.videoDao().getPopularVideos() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
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
