package com.rajatt7z.creamie.presentation.detail

import app.cash.turbine.test
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.data.repository.DownloadRepository
import com.rajatt7z.creamie.data.repository.WallpaperSetterRepository
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.model.WallpaperSrc
import com.rajatt7z.creamie.domain.repository.FavoritesRepository
import com.rajatt7z.creamie.domain.repository.PhotoRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import androidx.lifecycle.SavedStateHandle
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var photoRepository: PhotoRepository
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var downloadRepository: DownloadRepository
    private lateinit var wallpaperSetterRepository: WallpaperSetterRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private val testPhoto = Photo(
        id = 123,
        width = 1920,
        height = 1080,
        url = "https://pexels.com/photo/123",
        photographer = "John Doe",
        photographerUrl = "https://pexels.com/@john",
        photographerId = 456,
        avgColor = "#FFFFFF",
        src = WallpaperSrc(
            original = "https://images.pexels.com/photos/123/original.jpg",
            large2x = "https://images.pexels.com/photos/123/large2x.jpg",
            large = "https://images.pexels.com/photos/123/large.jpg",
            medium = "https://images.pexels.com/photos/123/medium.jpg",
            small = "https://images.pexels.com/photos/123/small.jpg",
            portrait = "https://images.pexels.com/photos/123/portrait.jpg",
            landscape = "https://images.pexels.com/photos/123/landscape.jpg",
            tiny = "https://images.pexels.com/photos/123/tiny.jpg"
        ),
        alt = "A beautiful sunset",
        liked = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        photoRepository = mockk()
        favoritesRepository = mockk()
        downloadRepository = mockk()
        wallpaperSetterRepository = mockk()
        savedStateHandle = SavedStateHandle(mapOf("photoId" to 123))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `photo loads successfully`() = runTest {
        coEvery { photoRepository.getPhotoById(123) } returns NetworkResult.Success(testPhoto)
        coEvery { favoritesRepository.isFavorite(123) } returns flowOf(false)

        val viewModel = DetailViewModel(
            savedStateHandle, photoRepository, favoritesRepository,
            downloadRepository, wallpaperSetterRepository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.photo)
            assertEquals("John Doe", state.photo?.photographer)
            assertNull(state.error)
        }
    }

    @Test
    fun `photo load error sets error state`() = runTest {
        coEvery { photoRepository.getPhotoById(123) } returns NetworkResult.Error("Not found")
        coEvery { favoritesRepository.isFavorite(123) } returns flowOf(false)

        val viewModel = DetailViewModel(
            savedStateHandle, photoRepository, favoritesRepository,
            downloadRepository, wallpaperSetterRepository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.error)
            assertEquals("Not found", state.error)
        }
    }

    @Test
    fun `toggleFavorite calls repository`() = runTest {
        coEvery { photoRepository.getPhotoById(123) } returns NetworkResult.Success(testPhoto)
        coEvery { favoritesRepository.isFavorite(123) } returns flowOf(false)
        coEvery { favoritesRepository.toggleFavorite(any()) } just Runs

        val viewModel = DetailViewModel(
            savedStateHandle, photoRepository, favoritesRepository,
            downloadRepository, wallpaperSetterRepository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { favoritesRepository.toggleFavorite(testPhoto) }
    }

    @Test
    fun `setSelectedQuality updates state`() = runTest {
        coEvery { photoRepository.getPhotoById(123) } returns NetworkResult.Success(testPhoto)
        coEvery { favoritesRepository.isFavorite(123) } returns flowOf(false)

        val viewModel = DetailViewModel(
            savedStateHandle, photoRepository, favoritesRepository,
            downloadRepository, wallpaperSetterRepository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setSelectedQuality("portrait")
        assertEquals("portrait", viewModel.uiState.value.selectedQuality)
    }

    @Test
    fun `favorite state is observed reactively`() = runTest {
        coEvery { photoRepository.getPhotoById(123) } returns NetworkResult.Success(testPhoto)
        coEvery { favoritesRepository.isFavorite(123) } returns flowOf(true)

        val viewModel = DetailViewModel(
            savedStateHandle, photoRepository, favoritesRepository,
            downloadRepository, wallpaperSetterRepository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isFavorite)
        }
    }

    @Test
    fun `showWallpaperDialog and dismissWallpaperDialog work`() = runTest {
        coEvery { photoRepository.getPhotoById(123) } returns NetworkResult.Success(testPhoto)
        coEvery { favoritesRepository.isFavorite(123) } returns flowOf(false)

        val viewModel = DetailViewModel(
            savedStateHandle, photoRepository, favoritesRepository,
            downloadRepository, wallpaperSetterRepository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showWallpaperDialog)

        viewModel.showWallpaperDialog()
        assertTrue(viewModel.uiState.value.showWallpaperDialog)

        viewModel.dismissWallpaperDialog()
        assertFalse(viewModel.uiState.value.showWallpaperDialog)
    }

    @Test
    fun `clearMessage nullifies message`() = runTest {
        coEvery { photoRepository.getPhotoById(123) } returns NetworkResult.Success(testPhoto)
        coEvery { favoritesRepository.isFavorite(123) } returns flowOf(false)

        val viewModel = DetailViewModel(
            savedStateHandle, photoRepository, favoritesRepository,
            downloadRepository, wallpaperSetterRepository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearMessage()
        assertNull(viewModel.uiState.value.message)
    }
}
