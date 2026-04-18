package com.rajatt7z.creamie.data.repository

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperSetterRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Downloads the image from URL and sets it as wallpaper.
     * @param flag WallpaperManager.FLAG_SYSTEM, FLAG_LOCK, or FLAG_SYSTEM or FLAG_LOCK
     */
    suspend fun setWallpaper(
        imageUrl: String,
        flag: Int,
        cropHint: android.graphics.Rect? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.setBitmap(bitmap, cropHint, true, flag)
            bitmap.recycle()

            val target = when (flag) {
                WallpaperManager.FLAG_SYSTEM -> "home screen"
                WallpaperManager.FLAG_LOCK -> "lock screen"
                else -> "both screens"
            }
            Result.success("Wallpaper set for $target!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sets wallpaper from an already-decoded Bitmap.
     */
    suspend fun setWallpaperFromBitmap(
        bitmap: Bitmap,
        flag: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.setBitmap(bitmap, null, true, flag)

            val target = when (flag) {
                WallpaperManager.FLAG_SYSTEM -> "home screen"
                WallpaperManager.FLAG_LOCK -> "lock screen"
                else -> "both screens"
            }
            Result.success("Wallpaper set for $target!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
