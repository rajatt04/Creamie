package com.rajatt7z.creamie.data.paging

import com.rajatt7z.creamie.data.mapper.toDomain
import com.rajatt7z.creamie.data.remote.PexelsApiService
import com.rajatt7z.creamie.data.remote.dto.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import androidx.paging.PagingSource
import androidx.paging.PagingConfig

@OptIn(ExperimentalCoroutinesApi::class)
class CuratedPagingSourceTest {

    private lateinit var apiService: PexelsApiService
    private lateinit var pagingSource: CuratedPagingSource

    private val mockPhotoDto = PhotoDto(
        id = 1,
        width = 1920,
        height = 1080,
        url = "https://pexels.com/photo/1",
        photographer = "Test Photographer",
        photographerUrl = "https://pexels.com/@test",
        photographerId = 100,
        avgColor = "#AABBCC",
        src = PhotoSrcDto(
            original = "https://images.pexels.com/1/original.jpg",
            large2x = "https://images.pexels.com/1/large2x.jpg",
            large = "https://images.pexels.com/1/large.jpg",
            medium = "https://images.pexels.com/1/medium.jpg",
            small = "https://images.pexels.com/1/small.jpg",
            portrait = "https://images.pexels.com/1/portrait.jpg",
            landscape = "https://images.pexels.com/1/landscape.jpg",
            tiny = "https://images.pexels.com/1/tiny.jpg"
        ),
        alt = "Test photo",
        liked = false
    )

    @Before
    fun setup() {
        apiService = mockk()
        pagingSource = CuratedPagingSource(apiService)
    }

    @Test
    fun `load returns page data on success`() = runTest {
        val photosResponse = PhotosResponseDto(
            totalResults = 1,
            page = 1,
            perPage = 15,
            photos = listOf(mockPhotoDto),
            nextPage = "https://api.pexels.com/v1/curated?page=2&per_page=15"
        )
        coEvery { apiService.getCuratedPhotos(page = 1, perPage = 15) } returns Response.success(photosResponse)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 15,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals("Test Photographer", page.data[0].photographer)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `load returns error on exception`() = runTest {
        coEvery { apiService.getCuratedPhotos(page = 1, perPage = 15) } throws RuntimeException("Network error")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 15,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `load returns null nextKey when no next page`() = runTest {
        val photosResponse = PhotosResponseDto(
            totalResults = 1,
            page = 1,
            perPage = 15,
            photos = listOf(mockPhotoDto),
            nextPage = null
        )
        coEvery { apiService.getCuratedPhotos(page = 1, perPage = 15) } returns Response.success(photosResponse)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 15,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertNull(page.nextKey)
    }
}
