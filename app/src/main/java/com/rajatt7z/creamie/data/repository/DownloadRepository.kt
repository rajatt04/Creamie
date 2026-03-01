package com.rajatt7z.creamie.data.repository

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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

        val request = DownloadManager.Request(Uri.parse(url))
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
     * Save wallpaper to MediaStore (preferred on Android 10+).
     */
    suspend fun saveToMediaStore(
        photo: Photo,
        imageBytes: ByteArray,
        quality: String = "original"
    ): Uri? = withContext(Dispatchers.IO) {
        val fileName = "creamie_${photo.id}_$quality.jpg"
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/${Constants.DOWNLOAD_FOLDER}"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(imageBytes)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(it, contentValues, null, null)
            }

            // Record download
            downloadHistoryDao.insertDownload(
                DownloadHistoryEntity(
                    photoId = photo.id,
                    photographer = photo.photographer,
                    filePath = it.toString(),
                    quality = quality,
                    fileSize = imageBytes.size.toLong()
                )
            )
        }
        uri
    }
}
