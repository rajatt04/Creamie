package com.rajatt7z.creamie.data.remote.dto

import com.google.gson.annotations.SerializedName

// ========== Photo DTOs ==========

data class PhotoResponseDto(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val photos: List<PhotoDto>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("next_page") val nextPage: String?
)

data class PhotoDto(
    val id: Int,
    val width: Int?,
    val height: Int?,
    val url: String?,
    val photographer: String?,
    @SerializedName("photographer_url") val photographerUrl: String?,
    @SerializedName("photographer_id") val photographerId: Long?,
    @SerializedName("avg_color") val avgColor: String?,
    val src: SrcDto?,
    val liked: Boolean?,
    val alt: String?
)

data class SrcDto(
    val original: String?,
    val large2x: String?,
    val large: String?,
    val medium: String?,
    val small: String?,
    val portrait: String?,
    val landscape: String?,
    val tiny: String?
)

// ========== Video DTOs ==========

data class VideoResponseDto(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val videos: List<VideoDto>,
    @SerializedName("total_results") val totalResults: Int,
    val url: String?,
    @SerializedName("next_page") val nextPage: String?
)

data class VideoDto(
    val id: Int,
    val width: Int?,
    val height: Int?,
    val url: String?,
    val image: String?, // Thumbnail URL
    val duration: Int?,
    val user: VideoUserDto?,
    @SerializedName("video_files") val videoFiles: List<VideoFileDto>?,
    @SerializedName("video_pictures") val videoPictures: List<VideoPictureDto>?
)

data class VideoUserDto(
    val id: Int,
    val name: String,
    val url: String
)

data class VideoFileDto(
    val id: Int,
    val quality: String, // "hd", "sd", "hls"
    @SerializedName("file_type") val fileType: String,
    val width: Int?,
    val height: Int?,
    val fps: Double?,
    val link: String
)

data class VideoPictureDto(
    val id: Int,
    val picture: String,
    val nr: Int
)

// ========== Collection DTOs ==========

data class CollectionListResponseDto(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val collections: List<CollectionDto>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("next_page") val nextPage: String?
)

data class CollectionDto(
    val id: String,
    val title: String,
    val description: String?,
    @SerializedName("private") val isPrivate: Boolean,
    @SerializedName("media_count") val mediaCount: Int,
    @SerializedName("photos_count") val photosCount: Int,
    @SerializedName("videos_count") val videosCount: Int
)

data class CollectionMediaResponseDto(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val media: List<CollectionMediaDto>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("next_page") val nextPage: String?,
    val id: String
)

data class CollectionMediaDto(
    val type: String, // "Photo" or "Video"
    val id: Int,
    val width: Int?,
    val height: Int?,
    val url: String?,
    val photographer: String?,
    @SerializedName("photographer_url") val photographerUrl: String?,
    val src: SrcDto?,
    // Video fields
    val image: String?,
    val duration: Int?,
    @SerializedName("video_files") val videoFiles: List<VideoFileDto>?
)
