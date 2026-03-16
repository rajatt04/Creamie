package com.rajatt7z.creamie.data.worker

import android.content.Context
import android.graphics.BitmapFactory
import android.app.WallpaperManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rajatt7z.creamie.data.remote.PexelsApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * WorkManager worker that automatically changes the wallpaper
 * at scheduled intervals using random curated photos from Pexels.
 */
@HiltWorker
class AutoWallpaperWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: PexelsApiService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "auto_wallpaper_changer"
        const val KEY_FLAG = "wallpaper_flag"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Fetch a random page of curated photos
            val randomPage = (1..50).random()
            val responseDto = apiService.getCuratedPhotos(page = randomPage, perPage = 1)

            val photo = responseDto.photos.firstOrNull()
                ?: return@withContext Result.retry()

            // Download the image
            val url = URL(photo.src?.portrait ?: "")
            val connection = url.openConnection()
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) return@withContext Result.retry()

            // Set wallpaper
            val wallpaperManager = WallpaperManager.getInstance(applicationContext)
            val flag = inputData.getInt(
                KEY_FLAG,
                WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
            )
            wallpaperManager.setBitmap(bitmap, null, true, flag)
            bitmap.recycle()

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
