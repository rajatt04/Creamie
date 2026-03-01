package com.rajatt7z.creamie.data.mapper

import com.rajatt7z.creamie.data.local.entity.FavoriteEntity
import com.rajatt7z.creamie.data.remote.dto.PhotoDto
import com.rajatt7z.creamie.data.remote.dto.PhotoSrcDto
import com.rajatt7z.creamie.data.remote.dto.CollectionDto
import org.junit.Assert.*
import org.junit.Test

class MappersTest {

    @Test
    fun `PhotoDto toDomain maps all fields correctly`() {
        val dto = PhotoDto(
            id = 42,
            width = 3840,
            height = 2160,
            url = "https://pexels.com/photo/42",
            photographer = "Jane Smith",
            photographerUrl = "https://pexels.com/@jane",
            photographerId = 99,
            avgColor = "#FF5500",
            src = PhotoSrcDto(
                original = "original.jpg",
                large2x = "large2x.jpg",
                large = "large.jpg",
                medium = "medium.jpg",
                small = "small.jpg",
                portrait = "portrait.jpg",
                landscape = "landscape.jpg",
                tiny = "tiny.jpg"
            ),
            alt = "Beautiful landscape",
            liked = true
        )

        val domain = dto.toDomain()

        assertEquals(42, domain.id)
        assertEquals(3840, domain.width)
        assertEquals(2160, domain.height)
        assertEquals("Jane Smith", domain.photographer)
        assertEquals("https://pexels.com/@jane", domain.photographerUrl)
        assertEquals("#FF5500", domain.avgColor)
        assertEquals("original.jpg", domain.src.original)
        assertEquals("large2x.jpg", domain.src.large2x)
        assertEquals("portrait.jpg", domain.src.portrait)
        assertEquals("Beautiful landscape", domain.alt)
        assertTrue(domain.liked)
    }

    @Test
    fun `FavoriteEntity toDomain maps correctly`() {
        val entity = FavoriteEntity(
            photoId = 100,
            photographer = "Alex",
            photographerUrl = "https://pexels.com/@alex",
            avgColor = "#000000",
            srcOriginal = "orig.jpg",
            srcLarge2x = "l2x.jpg",
            srcLarge = "l.jpg",
            srcMedium = "m.jpg",
            srcSmall = "s.jpg",
            srcPortrait = "p.jpg",
            srcLandscape = "ls.jpg",
            srcTiny = "t.jpg",
            alt = "Dark photo",
            width = 1080,
            height = 1920
        )

        val domain = entity.toDomain()

        assertEquals(100, domain.id)
        assertEquals("Alex", domain.photographer)
        assertEquals(1080, domain.width)
        assertEquals(1920, domain.height)
        assertEquals("orig.jpg", domain.src.original)
        assertEquals("Dark photo", domain.alt)
    }

    @Test
    fun `Photo toFavoriteEntity maps correctly`() {
        val dto = PhotoDto(
            id = 200,
            width = 800,
            height = 600,
            url = "url",
            photographer = "Bob",
            photographerUrl = "boburl",
            photographerId = 10,
            avgColor = "#112233",
            src = PhotoSrcDto(
                original = "o", large2x = "l2x", large = "l",
                medium = "m", small = "s",
                portrait = "p", landscape = "ls", tiny = "t"
            ),
            alt = "A photo",
            liked = false
        )

        val domain = dto.toDomain()
        val entity = domain.toFavoriteEntity()

        assertEquals(200, entity.photoId)
        assertEquals("Bob", entity.photographer)
        assertEquals("o", entity.srcOriginal)
        assertEquals("#112233", entity.avgColor)
    }

    @Test
    fun `CollectionDto toDomain maps correctly`() {
        val dto = CollectionDto(
            id = "col_abc",
            title = "My Collection",
            description = "A great collection",
            isPrivate = false,
            mediaCount = 25,
            photosCount = 20,
            videosCount = 5
        )

        val domain = dto.toDomain()

        assertEquals("col_abc", domain.id)
        assertEquals("My Collection", domain.title)
        assertEquals("A great collection", domain.description)
        assertFalse(domain.isPrivate)
        assertEquals(25, domain.mediaCount)
        assertEquals(20, domain.photosCount)
        assertEquals(5, domain.videosCount)
    }
}
