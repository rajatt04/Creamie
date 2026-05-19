package com.rajatt7z.creamie.data.repository

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.rajatt7z.creamie.core.common.Constants
import com.rajatt7z.creamie.data.local.dao.DownloadHistoryDao
import com.rajatt7z.creamie.data.local.entity.DownloadHistoryEntity
import com.rajatt7z.creamie.domain.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadHistoryDao: DownloadHistoryDao
) {

    /**
     * Download a wallpaper using Android DownloadManager.
     * Saves to Pictures/Creamie/ directory.
     * Returns the download ID for tracking progress.
     */
    suspend fun downloadWallpaper(
        photo: Photo,
        quality: String = "original"
    ): Long = withContext(Dispatchers.IO) {
        val url = photo.src.forQuality(quality)
        val fileName = "creamie_${photo.id}_$quality.jpg"

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(url.toUri())
            .setTitle("Downloading Wallpaper")
            .setDescription("by ${photo.photographer}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                "${Constants.DOWNLOAD_FOLDER}/$fileName"
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)

        val downloadId = downloadManager.enqueue(request)

        // Record download in history
        val filePath = "${Environment.DIRECTORY_PICTURES}/${Constants.DOWNLOAD_FOLDER}/$fileName"
        downloadHistoryDao.insertDownload(
            DownloadHistoryEntity(
                photoId = photo.id,
                photographer = photo.photographer,
                filePath = filePath,
                quality = quality
            )
        )

        downloadId
    }



    /**
     * Download a video using Android DownloadManager.
     * Saves to Movies/Creamie/ directory.
     */
    suspend fun downloadVideo(
        video: com.rajatt7z.creamie.domain.model.Video,
        quality: com.rajatt7z.creamie.domain.model.VideoFile
    ): Long = withContext(Dispatchers.IO) {
        val url = quality.link
        val fileName = "creamie_video_${video.id}_${quality.quality}.mp4"

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(url.toUri())
            .setTitle("Downloading Video")
            .setDescription("by ${video.user.name}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MOVIES,
                "${Constants.DOWNLOAD_FOLDER}/$fileName"
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)

        downloadManager.enqueue(request)
    }
}
