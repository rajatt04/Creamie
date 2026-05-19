package com.rajatt7z.creamie.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.rajatt7z.creamie.data.local.entity.VideoEntity

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY cachedAt ASC, id ASC")
    fun getPopularVideos(): PagingSource<Int, VideoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<VideoEntity>)

    @Query("DELETE FROM videos")
    suspend fun clearAll()
}
