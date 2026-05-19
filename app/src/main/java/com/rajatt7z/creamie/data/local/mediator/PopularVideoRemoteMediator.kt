package com.rajatt7z.creamie.data.local.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rajatt7z.creamie.data.local.CreamieDatabase
import com.rajatt7z.creamie.data.local.entity.RemoteKeyEntity
import com.rajatt7z.creamie.data.local.entity.VideoEntity
import com.rajatt7z.creamie.data.mapper.toEntity
import com.rajatt7z.creamie.data.remote.PexelsApiService

@OptIn(ExperimentalPagingApi::class)
class PopularVideoRemoteMediator(
    private val api: PexelsApiService,
    private val db: CreamieDatabase
) : RemoteMediator<Int, VideoEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VideoEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.remoteKeyDao().getRemoteKey("popular_videos")
                    val nextPage = remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    nextPage
                }
            }

            val response = api.getPopularVideos(page = page, perPage = state.config.pageSize)
            val videos = response.videos
            val endOfPaginationReached = videos.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().deleteByQuery("popular_videos")
                    db.videoDao().clearAll()
                }

                val nextPage = if (endOfPaginationReached) null else page + 1
                val prevPage = if (page == 1) null else page - 1

                db.remoteKeyDao().insertOrReplace(
                    RemoteKeyEntity(
                        queryOrCategory = "popular_videos",
                        nextPage = nextPage,
                        prevPage = prevPage
                    )
                )

                val currentTime = System.currentTimeMillis()
                val entities = videos.mapIndexed { index, video -> 
                    video.toEntity().copy(cachedAt = currentTime + index) 
                }
                db.videoDao().insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
