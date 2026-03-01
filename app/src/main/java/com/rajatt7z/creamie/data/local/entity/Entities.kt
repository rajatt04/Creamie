package com.rajatt7z.creamie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String,
    val photographerId: Long,
    val avgColor: String?,
    val srcOriginal: String,
    val srcLarge2x: String,
    val srcLarge: String,
    val srcMedium: String,
    val srcSmall: String,
    val srcPortrait: String,
    val srcLandscape: String,
    val srcTiny: String,
    val liked: Boolean,
    val alt: String,
    val queryOrCategory: String, // "curated" or the search query
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val isPrivate: Boolean,
    val mediaCount: Int,
    val photosCount: Int,
    val videosCount: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val photoId: Int,
    val width: Int = 0,
    val height: Int = 0,
    val photographer: String = "",
    val photographerUrl: String = "",
    val avgColor: String? = null,
    val srcOriginal: String = "",
    val srcLarge2x: String = "",
    val srcMedium: String = "",
    val srcSmall: String = "",
    val srcTiny: String = "",
    val alt: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "download_history")
data class DownloadHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoId: Int,
    val photographer: String,
    val filePath: String,
    val quality: String,
    val fileSize: Long = 0,
    val downloadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val queryOrCategory: String,
    val nextPage: Int?,
    val prevPage: Int?,
    val createdAt: Long = System.currentTimeMillis()
)
