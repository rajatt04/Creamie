package com.rajatt7z.creamie.repository

import com.rajatt7z.creamie.api.ApiClient
import com.rajatt7z.creamie.api.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository {
    private val api = ApiClient.api

    suspend fun searchPhotos(query: String, perPage: Int = 15): Result<List<Photo>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.searchPhotos(query, perPage)
                Result.success(response.photos)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}