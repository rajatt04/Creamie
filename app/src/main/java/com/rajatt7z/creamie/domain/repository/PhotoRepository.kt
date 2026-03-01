package com.rajatt7z.creamie.domain.repository

import androidx.paging.PagingData
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {

    fun getCuratedPhotos(): Flow<PagingData<Photo>>

    fun searchPhotos(
        query: String,
        orientation: String? = null,
        size: String? = null,
        color: String? = null,
        locale: String? = null
    ): Flow<PagingData<Photo>>

    suspend fun getPhotoById(id: Int): NetworkResult<Photo>
}
