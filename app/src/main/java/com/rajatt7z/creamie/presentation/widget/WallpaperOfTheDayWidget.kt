package com.rajatt7z.creamie.presentation.widget

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.color.ColorProvider
import com.rajatt7z.creamie.MainActivity
import com.rajatt7z.creamie.data.worker.DailyWallpaperWorker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Glance-based "Wallpaper of the Day" widget.
 * Shows a cached daily wallpaper with a subtle overlay.
 */
class WallpaperOfTheDayWidget : GlanceAppWidget() {

    companion object {
        private val SMALL = DpSize(130.dp, 130.dp)
        private val MEDIUM = DpSize(200.dp, 200.dp)
        private val LARGE = DpSize(300.dp, 200.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SMALL, MEDIUM, LARGE)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        val context = LocalContext.current
        val size = LocalSize.current

        val cacheFile = File(context.cacheDir, DailyWallpaperWorker.CACHE_FILE_NAME)
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        val today = dateFormat.format(Date())

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(24.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            // Background image
            if (cacheFile.exists()) {
                // Downsample image to prevent TransactionTooLargeException (Binder 1MB limit)
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(cacheFile.absolutePath, options)
                
                var inSampleSize = 1
                val reqWidth = 300
                val reqHeight = 300
                
                if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
                    val halfHeight = options.outHeight / 2
                    val halfWidth = options.outWidth / 2
                    // Use || instead of && to ensure we scale down if EITHER dimension is too large!
                    while (halfHeight / inSampleSize >= reqHeight || halfWidth / inSampleSize >= reqWidth) {
                        inSampleSize *= 2
                    }
                }
                
                val decodeOptions = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
                val bitmap = BitmapFactory.decodeFile(cacheFile.absolutePath, decodeOptions)
                
                if (bitmap != null) {
                    Image(
                        provider = ImageProvider(bitmap),
                        contentDescription = "Daily Wallpaper",
                        modifier = GlanceModifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Overlay content
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "WALLPAPER OF THE DAY",
                    style = TextStyle(
                        color = ColorProvider(
                            day = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                            night = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                if (size.width >= MEDIUM.width) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = today,
                        style = TextStyle(
                            color = ColorProvider(
                                day = androidx.compose.ui.graphics.Color.White,
                                night = androidx.compose.ui.graphics.Color.White
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "Tap to explore →",
                    style = TextStyle(
                        color = ColorProvider(
                            day = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                            night = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)
                        ),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

class WallpaperOfTheDayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WallpaperOfTheDayWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        
        // Enqueue the worker to fetch the image and update the widget
        val request = androidx.work.OneTimeWorkRequestBuilder<DailyWallpaperWorker>().build()
        androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
            "widget_update_work",
            androidx.work.ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
