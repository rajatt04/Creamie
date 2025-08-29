package com.rajatt7z.creamie.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

data class PhotoResponse(
    val photos: List<Photo>
)

data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val photographer: String, // Added photographer field
    val photographer_url: String, // Added photographer URL
    val photographer_id: Int, // Added photographer ID
    val src: Src,
    val liked: Boolean = false, // Added liked field
    val alt: String = "" // Added alt text field
)

data class Src(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)

interface PexelsApi {
    @Headers("Authorization: Eshv1BTmnWu0USg1WCla1OjzKELe1kiUbjFHAxuuLSyrCUXvSyBEtjS6")
    @GET("v1/search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 10
    ): PhotoResponse
}