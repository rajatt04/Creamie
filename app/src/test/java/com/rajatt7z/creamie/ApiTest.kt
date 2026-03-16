package com.rajatt7z.creamie

import com.rajatt7z.creamie.core.network.AuthInterceptor
import com.rajatt7z.creamie.data.remote.PexelsApiService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiTest {
    @Test
    fun testSearch() = runBlocking {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pexels.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PexelsApiService::class.java)
        
        try {
            println("Searching photos...")
            val photos = api.searchPhotos(query = "nature")
            println("Found photos: \${photos.photos.size}")
        } catch (e: Exception) {
            println("Error photos: \${e.message}")
            e.printStackTrace()
        }

        try {
            println("Searching videos...")
            val videos = api.searchVideos(query = "nature")
            println("Found videos: \${videos.videos.size}")
        } catch (e: Exception) {
            println("Error videos: \${e.message}")
            e.printStackTrace()
        }
    }
}
