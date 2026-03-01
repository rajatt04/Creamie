package com.rajatt7z.creamie.data.remote

import com.rajatt7z.creamie.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface covering all Pexels API endpoints.
 * Authorization header is added by AuthInterceptor.
 */
interface PexelsApiService {

    // ========== Photos ==========

    /** Curated photos - editorial picks by the Pexels team */
    @GET("v1/curated")
    suspend fun getCuratedPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): PhotoResponseDto

    /** Search photos with full filter support */
    @GET("v1/search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("orientation") orientation: String? = null,
        @Query("size") size: String? = null,
        @Query("color") color: String? = null,
        @Query("locale") locale: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): PhotoResponseDto

    /** Get a single photo by ID */
    @GET("v1/photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: Int
    ): PhotoDto

    // ========== Videos ==========

    /** Search videos */
    @GET("videos/search")
    suspend fun searchVideos(
        @Query("query") query: String,
        @Query("orientation") orientation: String? = null,
        @Query("size") size: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): VideoResponseDto

    /** Popular videos */
    @GET("videos/popular")
    suspend fun getPopularVideos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): VideoResponseDto

    /** Get a single video by ID */
    @GET("videos/videos/{id}")
    suspend fun getVideo(
        @Path("id") id: Int
    ): VideoDto

    // ========== Collections ==========

    /** All user collections */
    @GET("v1/collections")
    suspend fun getCollections(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): CollectionListResponseDto

    /** Featured collections */
    @GET("v1/collections/featured")
    suspend fun getFeaturedCollections(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): CollectionListResponseDto

    /** Get media from a specific collection */
    @GET("v1/collections/{id}")
    suspend fun getCollectionMedia(
        @Path("id") id: String,
        @Query("type") type: String? = null, // "photos" or "videos"
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): CollectionMediaResponseDto
}
