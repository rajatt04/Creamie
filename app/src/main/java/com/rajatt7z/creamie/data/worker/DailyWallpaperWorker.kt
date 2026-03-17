package com.rajatt7z.creamie.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rajatt7z.creamie.data.remote.PexelsApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

/**
 * Worker that prefetches and caches the "Daily Wallpaper"
 * for display in the widget and home screen.
 */
@HiltWorker
class DailyWallpaperWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: PexelsApiService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "daily_wallpaper_prefetch"
        const val CACHE_FILE_NAME = "daily_wallpaper.jpg"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val randomPage = (1..100).random()
            val responseDto = apiService.getCuratedPhotos(page = randomPage, perPage = 1)

            val photo = responseDto.photos.firstOrNull()
                ?: return@withContext Result.retry()

            // Download and cache
            val url = URL(photo.src?.landscape ?: "")
            val connection = url.openConnection()
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            val inputStream = connection.getInputStream()

            val cacheFile = File(applicationContext.cacheDir, CACHE_FILE_NAME)
            cacheFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
