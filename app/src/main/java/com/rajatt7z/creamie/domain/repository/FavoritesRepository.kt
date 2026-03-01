package com.rajatt7z.creamie.domain.repository

import com.rajatt7z.creamie.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getAllFavorites(): Flow<List<Photo>>

    fun isFavorite(photoId: Int): Flow<Boolean>

    suspend fun toggleFavorite(photo: Photo)

    fun getFavoriteCount(): Flow<Int>

    suspend fun clearAll()
}
