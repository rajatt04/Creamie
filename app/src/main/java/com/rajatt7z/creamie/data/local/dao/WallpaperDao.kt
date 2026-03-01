package com.rajatt7z.creamie.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.rajatt7z.creamie.data.local.entity.WallpaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {

    @Query("SELECT * FROM wallpapers WHERE queryOrCategory = :query ORDER BY cachedAt ASC")
    fun getWallpapersByQuery(query: String): PagingSource<Int, WallpaperEntity>

    @Query("SELECT * FROM wallpapers WHERE queryOrCategory = 'curated' ORDER BY cachedAt ASC")
    fun getCuratedWallpapers(): PagingSource<Int, WallpaperEntity>

    @Query("SELECT * FROM wallpapers WHERE id = :id")
    suspend fun getWallpaperById(id: Int): WallpaperEntity?

    @Query("SELECT * FROM wallpapers WHERE id = :id")
    fun observeWallpaperById(id: Int): Flow<WallpaperEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(wallpapers: List<WallpaperEntity>)

    @Query("DELETE FROM wallpapers WHERE queryOrCategory = :query")
    suspend fun clearByQuery(query: String)

    @Query("DELETE FROM wallpapers")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM wallpapers WHERE queryOrCategory = :query")
    suspend fun countByQuery(query: String): Int
}
