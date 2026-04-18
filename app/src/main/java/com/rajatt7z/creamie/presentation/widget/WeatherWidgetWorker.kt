package com.rajatt7z.creamie.presentation.widget

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class WeatherWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    override suspend fun doWork(): Result {
        return try {
            val hasPermission = ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            var lat = 40.7128
            var lon = -74.0060
            var locationName = "New York"

            if (hasPermission) {
                try {
                    val location = fusedLocationClient.lastLocation.await()
                    if (location != null) {
                        lat = location.latitude
                        lon = location.longitude
                        locationName = getCityName(lat, lon) ?: "Unknown Location"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val weatherData = fetchWeatherData(lat, lon)

            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(IosWeatherWidget::class.java)

            for (glanceId in glanceIds) {
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[WeatherWidgetKeys.locationName] = locationName
                    prefs[WeatherWidgetKeys.currentTemp] = weatherData.currentTemp
                    prefs[WeatherWidgetKeys.conditionIcon] = weatherData.conditionIcon
                    prefs[WeatherWidgetKeys.maxTemp] = weatherData.maxTemp
                    prefs[WeatherWidgetKeys.minTemp] = weatherData.minTemp
                    prefs[WeatherWidgetKeys.precipitationInfo] = weatherData.precipitationInfo
                    prefs[WeatherWidgetKeys.feelsLike] = weatherData.feelsLike
                }
                IosWeatherWidget().update(context, glanceId)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun getCityName(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchWeatherData(lat: Double, lon: Double): WeatherData = withContext(Dispatchers.IO) {
        val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon" +
                "&current=temperature_2m,apparent_temperature,precipitation,weather_code" +
                "&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max" +
                "&timezone=auto&temperature_unit=celsius"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        
        try {
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                
                val current = json.getJSONObject("current")
                val daily = json.getJSONObject("daily")
                
                val currentTemp = current.getDouble("temperature_2m").toInt()
                val feelsLike = current.getDouble("apparent_temperature").toInt()
                val weatherCode = current.getInt("weather_code")
                val icon = getWeatherIcon(weatherCode)
                
                val maxTemp = daily.getJSONArray("temperature_2m_max").getDouble(0).toInt()
                val minTemp = daily.getJSONArray("temperature_2m_min").getDouble(0).toInt()
                val precipProb = daily.getJSONArray("precipitation_probability_max").getInt(0)
                
                val precipInfo = if (precipProb > 0) "$precipProb% today" else "None today"
                
                return@withContext WeatherData(
                    currentTemp = currentTemp,
                    conditionIcon = icon,
                    maxTemp = maxTemp,
                    minTemp = minTemp,
                    precipitationInfo = precipInfo,
                    feelsLike = feelsLike
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }
        
        return@withContext WeatherData(25, "⛅", 30, 20, "No data", 26)
    }

    private fun getWeatherIcon(code: Int): String {
        return when (code) {
            0 -> "☀️"
            1, 2, 3 -> "⛅"
            45, 48 -> "🌫️"
            51, 53, 55, 56, 57 -> "🌧️"
            61, 63, 65, 66, 67 -> "🌧️"
            71, 73, 75, 77 -> "❄️"
            80, 81, 82 -> "🌧️"
            95, 96, 99 -> "⛈️"
            else -> "☁️"
        }
    }
}

data class WeatherData(
    val currentTemp: Int,
    val conditionIcon: String,
    val maxTemp: Int,
    val minTemp: Int,
    val precipitationInfo: String,
    val feelsLike: Int
)
