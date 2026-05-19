package com.rajatt7z.creamie.data.local.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rajatt7z.creamie.data.local.CreamieDatabase
import com.rajatt7z.creamie.data.local.entity.RemoteKeyEntity
import com.rajatt7z.creamie.data.local.entity.WallpaperEntity
import com.rajatt7z.creamie.data.mapper.toEntity
import com.rajatt7z.creamie.data.remote.PexelsApiService

@OptIn(ExperimentalPagingApi::class)
class CuratedRemoteMediator(
    private val api: PexelsApiService,
    private val db: CreamieDatabase
) : RemoteMediator<Int, WallpaperEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WallpaperEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.remoteKeyDao().getRemoteKey("curated")
                    val nextPage = remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    nextPage
                }
            }

            val response = api.getCuratedPhotos(page = page, perPage = state.config.pageSize)
            val photos = response.photos
            val endOfPaginationReached = photos.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().deleteByQuery("curated")
                    db.wallpaperDao().clearByQuery("curated")
                }

                val nextPage = if (endOfPaginationReached) null else page + 1
                val prevPage = if (page == 1) null else page - 1

                db.remoteKeyDao().insertOrReplace(
                    RemoteKeyEntity(
                        queryOrCategory = "curated",
                        nextPage = nextPage,
                        prevPage = prevPage
                    )
                )

                val currentTime = System.currentTimeMillis()
                val entities = photos.mapIndexed { index, photo -> 
                    photo.toEntity("curated").copy(cachedAt = currentTime + index) 
                }
                db.wallpaperDao().insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
