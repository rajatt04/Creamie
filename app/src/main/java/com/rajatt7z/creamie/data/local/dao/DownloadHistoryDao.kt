package com.rajatt7z.creamie.data.local.dao

import androidx.room.*
import com.rajatt7z.creamie.data.local.entity.DownloadHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadHistoryDao {

    @Query("SELECT * FROM download_history ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadHistoryEntity)

    @Query("DELETE FROM download_history WHERE id = :id")
    suspend fun deleteDownload(id: Long)

    @Query("DELETE FROM download_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM download_history")
    fun getDownloadCount(): Flow<Int>
}
