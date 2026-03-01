package com.rajatt7z.creamie.data.repository

import com.rajatt7z.creamie.data.local.dao.FavoriteDao
import com.rajatt7z.creamie.data.local.entity.FavoriteEntity
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.model.WallpaperSrc
import com.rajatt7z.creamie.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoritesRepository {

    override fun getAllFavorites(): Flow<List<Photo>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun isFavorite(photoId: Int): Flow<Boolean> {
        return favoriteDao.isFavorite(photoId)
    }

    override suspend fun toggleFavorite(photo: Photo) {
        val isFav = favoriteDao.isFavoriteSync(photo.id)
        if (isFav) {
            favoriteDao.removeFavorite(photo.id)
        } else {
            favoriteDao.addFavorite(photo.toEntity())
        }
    }

    override fun getFavoriteCount(): Flow<Int> {
        return favoriteDao.getFavoriteCount()
    }

    override suspend fun clearAll() {
        favoriteDao.clearAll()
    }

    private fun FavoriteEntity.toDomain(): Photo = Photo(
        id = photoId,
        width = width,
        height = height,
        url = "",
        photographer = photographer,
        photographerUrl = photographerUrl,
        photographerId = 0L,
        avgColor = avgColor,
        src = WallpaperSrc(
            original = srcOriginal,
            large2x = srcLarge2x,
            large = "",
            medium = srcMedium,
            small = srcSmall,
            portrait = "",
            landscape = "",
            tiny = srcTiny
        ),
        liked = true,
        alt = alt
    )

    private fun Photo.toEntity(): FavoriteEntity = FavoriteEntity(
        photoId = id,
        width = width,
        height = height,
        photographer = photographer,
        photographerUrl = photographerUrl,
        avgColor = avgColor,
        srcOriginal = src.original,
        srcLarge2x = src.large2x,
        srcMedium = src.medium,
        srcSmall = src.small,
        srcTiny = src.tiny,
        alt = alt
    )
}
