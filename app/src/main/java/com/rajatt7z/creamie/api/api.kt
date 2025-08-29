package com.rajatt7z.creamie.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

data class PhotoResponse(
    val photos: List<Photo>
)

data class Photo(
    val id: Int,
    val src: Src
)

data class Src(
    val medium: String,
    val large: String
)

interface PexelsApi {
    @Headers("Authorization: Eshv1BTmnWu0USg1WCla1OjzKELe1kiUbjFHAxuuLSyrCUXvSyBEtjS6")
    @GET("v1/search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 10
    ): PhotoResponse
}