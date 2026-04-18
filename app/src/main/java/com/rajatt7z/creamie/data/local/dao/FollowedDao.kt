package com.rajatt7z.creamie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rajatt7z.creamie.data.local.entity.FollowedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowedDao {
    @Query("SELECT * FROM follows ORDER BY followedAt DESC")
    fun getAllFollows(): Flow<List<FollowedEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM follows WHERE photographerId = :photographerId)")
    fun isFollowed(photographerId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(followed: FollowedEntity)

    @Query("DELETE FROM follows WHERE photographerId = :photographerId")
    suspend fun deleteFollow(photographerId: Long)
}
