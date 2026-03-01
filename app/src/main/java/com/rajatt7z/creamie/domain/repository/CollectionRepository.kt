package com.rajatt7z.creamie.domain.repository

import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.domain.model.Collection
import com.rajatt7z.creamie.domain.model.Photo
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {

    suspend fun getFeaturedCollections(): NetworkResult<List<Collection>>

    suspend fun getCollections(page: Int = 1): NetworkResult<List<Collection>>

    fun getCollectionMedia(collectionId: String): Flow<PagingData<Photo>>
}
