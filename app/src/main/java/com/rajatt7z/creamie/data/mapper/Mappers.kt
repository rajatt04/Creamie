package com.rajatt7z.creamie.data.mapper

import com.rajatt7z.creamie.data.local.entity.*
import com.rajatt7z.creamie.data.remote.dto.*
import com.rajatt7z.creamie.domain.model.*
import com.rajatt7z.creamie.domain.model.Collection

// ========== Photo Mappers ==========

fun PhotoDto.toDomain(): Photo = Photo(
    id = id,
    width = width ?: 0,
    height = height ?: 0,
    url = url ?: "",
    photographer = photographer ?: "Unknown",
    photographerUrl = photographerUrl ?: "",
    photographerId = photographerId ?: 0L,
    avgColor = avgColor,
    src = src?.toDomain() ?: WallpaperSrc("", "", "", "", "", "", "", ""),
    liked = liked ?: false,
    alt = alt ?: ""
)

fun SrcDto.toDomain(): WallpaperSrc = WallpaperSrc(
    original = original ?: "",
    large2x = large2x ?: "",
    large = large ?: "",
    medium = medium ?: "",
    small = small ?: "",
    portrait = portrait ?: "",
    landscape = landscape ?: "",
    tiny = tiny ?: ""
)

fun PhotoDto.toEntity(queryOrCategory: String = "curated"): WallpaperEntity = WallpaperEntity(
    id = id,
    width = width ?: 0,
    height = height ?: 0,
    url = url ?: "",
    photographer = photographer ?: "Unknown",
    photographerUrl = photographerUrl ?: "",
    photographerId = photographerId ?: 0L,
    avgColor = avgColor,
    srcOriginal = src?.original ?: "",
    srcLarge2x = src?.large2x ?: "",
    srcLarge = src?.large ?: "",
    srcMedium = src?.medium ?: "",
    srcSmall = src?.small ?: "",
    srcPortrait = src?.portrait ?: "",
    srcLandscape = src?.landscape ?: "",
    srcTiny = src?.tiny ?: "",
    liked = liked ?: false,
    alt = alt ?: "",
    queryOrCategory = queryOrCategory
)

fun WallpaperEntity.toDomain(): Photo = Photo(
    id = id,
    width = width,
    height = height,
    url = url,
    photographer = photographer,
    photographerUrl = photographerUrl,
    photographerId = photographerId,
    avgColor = avgColor,
    src = WallpaperSrc(
        original = srcOriginal,
        large2x = srcLarge2x,
        large = srcLarge,
        medium = srcMedium,
        small = srcSmall,
        portrait = srcPortrait,
        landscape = srcLandscape,
        tiny = srcTiny
    ),
    liked = liked,
    alt = alt
)

// ========== Video Mappers ==========

fun VideoDto.toDomain(): Video = Video(
    id = id,
    width = width ?: 0,
    height = height ?: 0,
    url = url ?: "",
    image = image ?: "",
    duration = duration ?: 0,
    user = Photographer(
        id = user?.id ?: 0,
        name = user?.name ?: "Unknown",
        url = user?.url ?: ""
    ),
    videoFiles = videoFiles?.map { it.toDomain() } ?: emptyList(),
    thumbnails = videoPictures?.map { it.picture } ?: emptyList()
)

fun VideoFileDto.toDomain(): VideoFile = VideoFile(
    id = id,
    quality = quality,
    fileType = fileType,
    width = width,
    height = height,
    link = link
)

// ========== Collection Mappers ==========

fun CollectionDto.toDomain(): Collection = Collection(
    id = id,
    title = title,
    description = description,
    isPrivate = isPrivate,
    mediaCount = mediaCount,
    photosCount = photosCount,
    videosCount = videosCount
)

fun CollectionDto.toEntity(): CollectionEntity = CollectionEntity(
    id = id,
    title = title,
    description = description,
    isPrivate = isPrivate,
    mediaCount = mediaCount,
    photosCount = photosCount,
    videosCount = videosCount
)

fun CollectionEntity.toDomain(): Collection = Collection(
    id = id,
    title = title,
    description = description,
    isPrivate = isPrivate,
    mediaCount = mediaCount,
    photosCount = photosCount,
    videosCount = videosCount
)

// ========== Collection Media → Photo ==========

fun CollectionMediaDto.toPhotoDomain(): Photo? {
    return when (type) {
        "Photo" -> {
            if (src == null) return null
            Photo(
                id = id,
                width = width ?: 0,
                height = height ?: 0,
                url = url ?: "",
                photographer = photographer ?: "Unknown",
                photographerUrl = photographerUrl ?: "",
                photographerId = 0L,
                avgColor = null,
                src = src.toDomain(),
                liked = false,
                alt = "",
                isVideo = false
            )
        }
        "Video" -> {
            val thumbnailUrl = image ?: return null
            Photo(
                id = id,
                width = width ?: 0,
                height = height ?: 0,
                url = url ?: "",
                photographer = photographer ?: "Unknown",
                photographerUrl = photographerUrl ?: "",
                photographerId = 0L,
                avgColor = null,
                src = WallpaperSrc(
                    original = thumbnailUrl,
                    large2x = thumbnailUrl,
                    large = thumbnailUrl,
                    medium = thumbnailUrl,
                    small = thumbnailUrl,
                    portrait = thumbnailUrl,
                    landscape = thumbnailUrl,
                    tiny = thumbnailUrl
                ),
                liked = false,
                alt = "",
                isVideo = true
            )
        }
        else -> null
    }
}
