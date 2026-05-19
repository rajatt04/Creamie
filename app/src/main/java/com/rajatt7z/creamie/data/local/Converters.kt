package com.rajatt7z.creamie.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rajatt7z.creamie.domain.model.VideoFile

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromVideoFileList(value: List<VideoFile>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toVideoFileList(value: String): List<VideoFile> {
        val listType = object : TypeToken<List<VideoFile>>() {}.type
        return try {
            gson.fromJson(value, listType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, listType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
