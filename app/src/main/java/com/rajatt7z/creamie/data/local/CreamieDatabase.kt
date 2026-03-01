package com.rajatt7z.creamie.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rajatt7z.creamie.data.local.dao.*
import com.rajatt7z.creamie.data.local.entity.*

@Database(
    entities = [
        WallpaperEntity::class,
        CollectionEntity::class,
        FavoriteEntity::class,
        SearchHistoryEntity::class,
        DownloadHistoryEntity::class,
        RemoteKeyEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class CreamieDatabase : RoomDatabase() {
    abstract fun wallpaperDao(): WallpaperDao
    abstract fun collectionDao(): CollectionDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun downloadHistoryDao(): DownloadHistoryDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}
