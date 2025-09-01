package com.rajatt7z.creamie.data

import android.content.Context
import android.content.SharedPreferences

object LikeStorage {
    private const val PREF_NAME = "likes_prefs"
    private const val KEY_LIKED_PHOTOS = "liked_photos"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun toggleLike(context: Context, photoUrl: String) {
        val prefs = prefs(context)
        val likes = prefs.getStringSet(KEY_LIKED_PHOTOS, mutableSetOf())!!.toMutableSet()
        if (likes.contains(photoUrl)) {
            likes.remove(photoUrl)
        } else {
            likes.add(photoUrl)
        }
        prefs.edit().putStringSet(KEY_LIKED_PHOTOS, likes).apply()
    }

    fun isLiked(context: Context, photoUrl: String): Boolean {
        return prefs(context).getStringSet(KEY_LIKED_PHOTOS, emptySet())!!.contains(photoUrl)
    }

    fun getLikedPhotos(context: Context): Set<String> {
        return prefs(context).getStringSet(KEY_LIKED_PHOTOS, emptySet()) ?: emptySet()
    }
}