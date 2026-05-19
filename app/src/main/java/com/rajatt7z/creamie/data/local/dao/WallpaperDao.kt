package com.rajatt7z.creamie.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rajatt7z.creamie.data.local.entity.WallpaperEntity

@Dao
interface WallpaperDao {



    @Query("SELECT * FROM wallpapers WHERE queryOrCategory = 'curated' ORDER BY cachedAt ASC, id ASC")
    fun getCuratedWallpapers(): PagingSource<Int, WallpaperEntity>





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(wallpapers: List<WallpaperEntity>)

    @Query("DELETE FROM wallpapers WHERE queryOrCategory = :query")
    suspend fun clearByQuery(query: String)

    @Query("DELETE FROM wallpapers")
    suspend fun clearAll()


}
