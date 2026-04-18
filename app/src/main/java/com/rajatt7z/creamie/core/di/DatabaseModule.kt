package com.rajatt7z.creamie.core.di

import android.content.Context
import androidx.room.Room
import com.rajatt7z.creamie.data.local.CreamieDatabase
import com.rajatt7z.creamie.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CreamieDatabase {
        return Room.databaseBuilder(
            context,
            CreamieDatabase::class.java,
            "creamie_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideWallpaperDao(db: CreamieDatabase): WallpaperDao = db.wallpaperDao()

    @Provides
    fun provideCollectionDao(db: CreamieDatabase): CollectionDao = db.collectionDao()

    @Provides
    fun provideFavoriteDao(db: CreamieDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideSearchHistoryDao(db: CreamieDatabase): SearchHistoryDao = db.searchHistoryDao()

    @Provides
    fun provideDownloadHistoryDao(db: CreamieDatabase): DownloadHistoryDao = db.downloadHistoryDao()

    @Provides
    fun provideRemoteKeyDao(db: CreamieDatabase): RemoteKeyDao = db.remoteKeyDao()

    @Provides
    fun provideFollowedDao(db: CreamieDatabase): FollowedDao = db.followedDao()
}
