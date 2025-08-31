package com.rajatt7z.creamie.repository

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.graphics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class WallpaperRepository {

    suspend fun downloadBitmap(imageUrl: String): Bitmap = withContext(Dispatchers.IO) {
        BitmapFactory.decodeStream(URL(imageUrl).openStream())
    }

    suspend fun setWallpaper(
        context: Context,
        bitmap: Bitmap,
        flag: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.setBitmap(bitmap, null, true, flag)

            val wallpaperType = when (flag) {
                WallpaperManager.FLAG_SYSTEM -> "home screen"
                WallpaperManager.FLAG_LOCK -> "lock screen"
                else -> "both screens"
            }
            Result.success("Wallpaper set for $wallpaperType!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("UseKtx")
    fun processBitmap(
        originalBitmap: Bitmap,
        screenWidth: Int,
        screenHeight: Int,
        opacity: Float,
        themeTint: Boolean,
        scale: Float,
        offsetX: Float,
        offsetY: Float
    ): Bitmap {
        val processedBitmap = Bitmap.createBitmap(
            screenWidth,
            screenHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(processedBitmap)

        val paint = Paint().apply {
            alpha = (opacity * 255).toInt()
            isAntiAlias = true
        }

        if (themeTint) {
            val tintColor = Color.argb(
                (0.3f * 255).toInt(),
                100,
                150,
                255
            )
            val colorFilter = PorterDuffColorFilter(
                tintColor,
                PorterDuff.Mode.OVERLAY
            )
            paint.colorFilter = colorFilter
        }

        val scaledWidth = (originalBitmap.width * scale).toInt()
        val scaledHeight = (originalBitmap.height * scale).toInt()

        val scaledBitmap = if (scale != 1f) {
            Bitmap.createScaledBitmap(
                originalBitmap,
                scaledWidth,
                scaledHeight,
                true
            )
        } else {
            originalBitmap
        }

        val centerX = (screenWidth - scaledWidth) / 2f + offsetX
        val centerY = (screenHeight - scaledHeight) / 2f + offsetY

        canvas.drawBitmap(scaledBitmap, centerX, centerY, paint)

        if (scaledBitmap != originalBitmap) {
            scaledBitmap.recycle()
        }

        return processedBitmap
    }
}