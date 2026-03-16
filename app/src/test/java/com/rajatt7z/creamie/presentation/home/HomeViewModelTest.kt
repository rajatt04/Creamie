package com.rajatt7z.creamie.presentation.home

import app.cash.turbine.test
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.domain.model.Collection
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.model.WallpaperSrc
import com.rajatt7z.creamie.domain.repository.CollectionRepository
import com.rajatt7z.creamie.domain.repository.PhotoRepository
import com.rajatt7z.creamie.domain.repository.VideoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var photoRepository: PhotoRepository
    private lateinit var collectionRepository: CollectionRepository
    private lateinit var videoRepository: VideoRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        photoRepository = mockk()
        collectionRepository = mockk()
        videoRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has loading collections`() = runTest {
        coEvery { collectionRepository.getFeaturedCollections() } returns NetworkResult.Success(emptyList())
        coEvery { photoRepository.getCuratedPhotos() } returns flowOf()

        viewModel = HomeViewModel(photoRepository, collectionRepository, videoRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isCollectionsLoading) // loaded
            assertTrue(state.featuredCollections.isEmpty())
        }
    }

    @Test
    fun `featured collections loaded successfully`() = runTest {
        val mockCollections = listOf(
            Collection(
                id = "abc123",
                title = "Nature",
                description = "Nature photos",
                isPrivate = false,
                mediaCount = 50,
                photosCount = 50,
                videosCount = 0
            )
        )
        coEvery { collectionRepository.getFeaturedCollections() } returns NetworkResult.Success(mockCollections)
        coEvery { photoRepository.getCuratedPhotos() } returns flowOf()

        viewModel = HomeViewModel(photoRepository, collectionRepository, videoRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.featuredCollections.size)
            assertEquals("Nature", state.featuredCollections.first().title)
            assertFalse(state.isCollectionsLoading)
            assertNull(state.collectionsError)
        }
    }

    @Test
    fun `featured collections error sets error message`() = runTest {
        coEvery { collectionRepository.getFeaturedCollections() } returns NetworkResult.Error("Network error")
        coEvery { photoRepository.getCuratedPhotos() } returns flowOf()

        viewModel = HomeViewModel(photoRepository, collectionRepository, videoRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.collectionsError)
            assertEquals("Network error", state.collectionsError)
            assertFalse(state.isCollectionsLoading)
        }
    }

    @Test
    fun `trending searches are pre-populated`() = runTest {
        coEvery { collectionRepository.getFeaturedCollections() } returns NetworkResult.Success(emptyList())
        coEvery { photoRepository.getCuratedPhotos() } returns flowOf()

        viewModel = HomeViewModel(photoRepository, collectionRepository, videoRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.trendingSearches.isNotEmpty())
            assertTrue(state.trendingSearches.contains("Nature"))
        }
    }
}
