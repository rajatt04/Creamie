package com.rajatt7z.creamie.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String,
    val photographerId: Long,
    val avgColor: String?,
    val src: WallpaperSrc,
    val liked: Boolean,
    val alt: String,
    val isVideo: Boolean = false
)

@Immutable
data class WallpaperSrc(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
) {
    /** Get URL for a given quality string */
    fun forQuality(quality: String): String = when (quality) {
        "original" -> original
        "large2x" -> large2x
        "large" -> large
        "medium" -> medium
        "small" -> small
        "portrait" -> portrait
        "landscape" -> landscape
        "tiny" -> tiny
        else -> original
    }
}

@Immutable
data class Photographer(
    val id: Int,
    val name: String,
    val url: String
)

@Immutable
data class Video(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val image: String,
    val duration: Int,
    val user: Photographer,
    val videoFiles: List<VideoFile>,
    val thumbnails: List<String>
)

@Immutable
data class VideoFile(
    val id: Int,
    val quality: String,
    val fileType: String,
    val width: Int?,
    val height: Int?,
    val link: String
)

@Immutable
data class Collection(
    val id: String,
    val title: String,
    val description: String?,
    val isPrivate: Boolean,
    val mediaCount: Int,
    val photosCount: Int,
    val videosCount: Int
)

@Immutable
data class FollowedPhotographer(
    val id: Long,
    val name: String,
    val url: String
)
