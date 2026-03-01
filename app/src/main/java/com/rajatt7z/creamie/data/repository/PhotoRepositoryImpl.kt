package com.rajatt7z.creamie.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rajatt7z.creamie.core.common.Constants
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.core.network.safeApiCall
import com.rajatt7z.creamie.data.mapper.toDomain
import com.rajatt7z.creamie.data.paging.CuratedPagingSource
import com.rajatt7z.creamie.data.paging.SearchPagingSource
import com.rajatt7z.creamie.data.remote.PexelsApiService
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val apiService: PexelsApiService
) : PhotoRepository {

    override fun getCuratedPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE,
                initialLoadSize = Constants.INITIAL_LOAD_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CuratedPagingSource(apiService) }
        ).flow
    }

    override fun searchPhotos(
        query: String,
        orientation: String?,
        size: String?,
        color: String?,
        locale: String?
    ): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE,
                initialLoadSize = Constants.INITIAL_LOAD_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(
                    apiService = apiService,
                    query = query,
                    orientation = orientation,
                    size = size,
                    color = color,
                    locale = locale
                )
            }
        ).flow
    }

    override suspend fun getPhotoById(id: Int): NetworkResult<Photo> {
        return safeApiCall {
            apiService.getPhoto(id).toDomain()
        }
    }
}
