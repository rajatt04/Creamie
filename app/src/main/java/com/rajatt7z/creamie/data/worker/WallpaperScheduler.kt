package com.rajatt7z.creamie.data.worker

import android.content.Context
import android.app.WallpaperManager
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Schedules and cancels the automatic wallpaper changer.
 */
object WallpaperScheduler {

    /**
     * Schedule periodic wallpaper changes.
     * @param context Application context
     * @param intervalHours Interval between wallpaper changes in hours
     * @param flag WallpaperManager flag (FLAG_SYSTEM, FLAG_LOCK, or both)
     */
    fun schedule(
        context: Context,
        intervalHours: Int = 6,
        flag: Int = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val inputData = Data.Builder()
            .putInt(AutoWallpaperWorker.KEY_FLAG, flag)
            .build()

        val request = PeriodicWorkRequestBuilder<AutoWallpaperWorker>(
            intervalHours.toLong(), TimeUnit.HOURS,
            15, TimeUnit.MINUTES // flex interval
        )
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(AutoWallpaperWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                AutoWallpaperWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }

    /**
     * Cancel the automatic wallpaper changer.
     */
    fun cancel(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(AutoWallpaperWorker.WORK_NAME)
    }

    /**
     * Change wallpaper immediately (one-time).
     */
    fun changeNow(
        context: Context,
        flag: Int = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putInt(AutoWallpaperWorker.KEY_FLAG, flag)
            .build()

        val request = OneTimeWorkRequestBuilder<AutoWallpaperWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag("${AutoWallpaperWorker.WORK_NAME}_instant")
            .build()

        WorkManager.getInstance(context)
            .enqueue(request)
    }
}
