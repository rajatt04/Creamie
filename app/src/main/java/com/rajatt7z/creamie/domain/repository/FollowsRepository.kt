package com.rajatt7z.creamie.domain.repository

import com.rajatt7z.creamie.domain.model.FollowedPhotographer
import kotlinx.coroutines.flow.Flow

interface FollowsRepository {
    fun getAllFollows(): Flow<List<FollowedPhotographer>>
    fun isFollowed(photographerId: Long): Flow<Boolean>
    suspend fun toggleFollow(photographerId: Long, name: String, url: String)
}
