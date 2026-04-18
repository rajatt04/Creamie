package com.rajatt7z.creamie.data.repository

import com.rajatt7z.creamie.data.local.dao.FollowedDao
import com.rajatt7z.creamie.data.local.entity.FollowedEntity
import com.rajatt7z.creamie.domain.model.FollowedPhotographer
import com.rajatt7z.creamie.domain.repository.FollowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FollowsRepositoryImpl @Inject constructor(
    private val followedDao: FollowedDao
) : FollowsRepository {

    override fun getAllFollows(): Flow<List<FollowedPhotographer>> {
        return followedDao.getAllFollows().map { entities ->
            entities.map { 
                FollowedPhotographer(
                    id = it.photographerId,
                    name = it.name,
                    url = it.url
                ) 
            }
        }
    }

    override fun isFollowed(photographerId: Long): Flow<Boolean> {
        return followedDao.isFollowed(photographerId)
    }

    override suspend fun toggleFollow(photographerId: Long, name: String, url: String) {
        val currentlyFollowed = followedDao.isFollowed(photographerId).first()
        if (currentlyFollowed) {
            followedDao.deleteFollow(photographerId)
        } else {
            followedDao.insertFollow(
                FollowedEntity(
                    photographerId = photographerId,
                    name = name,
                    url = url
                )
            )
        }
    }
}
