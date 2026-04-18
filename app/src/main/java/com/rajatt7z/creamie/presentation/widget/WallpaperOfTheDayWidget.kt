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
import androidx.glance.ColorFilter
import com.rajatt7z.creamie.MainActivity
import com.rajatt7z.creamie.R
import com.rajatt7z.creamie.data.worker.DailyWallpaperWorker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import android.content.Intent
import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition

/**
 * Glance-based "Wallpaper of the Day" widget.
 * Shows a cached daily wallpaper with a subtle overlay.
 */
class WallpaperOfTheDayWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

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
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val context = LocalContext.current
        val size = LocalSize.current

        val cacheFile = File(context.cacheDir, DailyWallpaperWorker.CACHE_FILE_NAME)
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        val today = dateFormat.format(Date())

        val photoId = currentState<Preferences>()[intPreferencesKey("photo_id")]
        
        val clickModifier = if (photoId != null) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("creamie://wallpaper/$photoId")
            ).apply {
                setPackage(context.packageName)
            }
            GlanceModifier.clickable(androidx.glance.appwidget.action.actionStartActivity(intent))
        } else {
            GlanceModifier.clickable(actionStartActivity<MainActivity>())
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(24.dp)
                .then(clickModifier)
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

            // Refresh Button at Top Right
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(32.dp)
                        .background(GlanceTheme.colors.surfaceVariant)
                        .cornerRadius(16.dp)
                        .clickable(androidx.glance.appwidget.action.actionRunCallback<RefreshWallpaperAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_refresh_widget),
                        contentDescription = "Refresh Wallpaper",
                        modifier = GlanceModifier.size(18.dp),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant)
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
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                if (size.width >= MEDIUM.width) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = today,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "Tap to explore →",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
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

class RefreshWallpaperAction : androidx.glance.appwidget.action.ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: androidx.glance.action.ActionParameters
    ) {
        val request = androidx.work.OneTimeWorkRequestBuilder<DailyWallpaperWorker>().build()
        androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
            "widget_update_work",
            androidx.work.ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
