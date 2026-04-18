package com.rajatt7z.creamie.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rajatt7z.creamie.core.common.Constants
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.core.network.safeApiCall
import com.rajatt7z.creamie.data.mapper.toDomain
import com.rajatt7z.creamie.data.paging.CollectionMediaPagingSource
import com.rajatt7z.creamie.data.remote.PexelsApiService
import com.rajatt7z.creamie.domain.model.Collection
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val apiService: PexelsApiService
) : CollectionRepository {

    override suspend fun getFeaturedCollections(): NetworkResult<List<Collection>> {
        return safeApiCall {
            apiService.getFeaturedCollections(page = 1, perPage = 20)
                .collections.map { it.toDomain() }
        }
    }

    override suspend fun getCollections(page: Int): NetworkResult<List<Collection>> {
        return safeApiCall {
            apiService.getCollections(page = page, perPage = 20)
                .collections.map { it.toDomain() }
        }
    }

    override fun getCollectionMedia(collectionId: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CollectionMediaPagingSource(apiService, collectionId)
            }
        ).flow
    }

    override fun getPagedCollections(): Flow<PagingData<Collection>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                com.rajatt7z.creamie.data.remote.paging.CollectionPagingSource(apiService)
            }
        ).flow
    }
}
