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
        RemoteKeyEntity::class,
        FollowedEntity::class,
        VideoEntity::class
    ],
    version = 5,
    exportSchema = true
)
@androidx.room.TypeConverters(Converters::class)
abstract class CreamieDatabase : RoomDatabase() {
    abstract fun wallpaperDao(): WallpaperDao
    abstract fun collectionDao(): CollectionDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun downloadHistoryDao(): DownloadHistoryDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun followedDao(): FollowedDao
    abstract fun videoDao(): VideoDao
}
