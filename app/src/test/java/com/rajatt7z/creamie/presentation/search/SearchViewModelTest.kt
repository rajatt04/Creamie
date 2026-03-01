package com.rajatt7z.creamie.presentation.search

import app.cash.turbine.test
import com.rajatt7z.creamie.data.local.dao.SearchHistoryDao
import com.rajatt7z.creamie.data.local.entity.SearchHistoryEntity
import com.rajatt7z.creamie.domain.repository.PhotoRepository
import io.mockk.*
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var photoRepository: PhotoRepository
    private lateinit var searchHistoryDao: SearchHistoryDao
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        photoRepository = mockk()
        searchHistoryDao = mockk()
        coEvery { searchHistoryDao.getRecentSearches() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty query`() = runTest {
        viewModel = SearchViewModel(photoRepository, searchHistoryDao)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.query)
            assertFalse(state.isFilterSheetVisible)
        }
    }

    @Test
    fun `onQueryChange updates query in state`() = runTest {
        viewModel = SearchViewModel(photoRepository, searchHistoryDao)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onQueryChange("sunset")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("sunset", state.query)
        }
    }

    @Test
    fun `onSearch saves to search history`() = runTest {
        coEvery { searchHistoryDao.insertSearch(any()) } just Runs

        viewModel = SearchViewModel(photoRepository, searchHistoryDao)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearch("mountains")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { searchHistoryDao.insertSearch(match { it.query == "mountains" }) }
    }

    @Test
    fun `onSearch with blank query does nothing`() = runTest {
        viewModel = SearchViewModel(photoRepository, searchHistoryDao)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearch("   ")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { searchHistoryDao.insertSearch(any()) }
    }

    @Test
    fun `onClearAllHistory calls dao clearAll`() = runTest {
        coEvery { searchHistoryDao.clearAll() } just Runs

        viewModel = SearchViewModel(photoRepository, searchHistoryDao)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onClearAllHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { searchHistoryDao.clearAll() }
    }

    @Test
    fun `toggleFilterSheet toggles visibility`() = runTest {
        viewModel = SearchViewModel(photoRepository, searchHistoryDao)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isFilterSheetVisible)

        viewModel.toggleFilterSheet()
        assertTrue(viewModel.uiState.value.isFilterSheetVisible)

        viewModel.toggleFilterSheet()
        assertFalse(viewModel.uiState.value.isFilterSheetVisible)
    }

    @Test
    fun `updateFilters updates filter state`() = runTest {
        viewModel = SearchViewModel(photoRepository, searchHistoryDao)
        testDispatcher.scheduler.advanceUntilIdle()

        val filters = SearchFilters(
            orientation = "landscape",
            size = "large",
            color = "red"
        )
        viewModel.updateFilters(filters)

        assertEquals("landscape", viewModel.uiState.value.filters.orientation)
        assertEquals("large", viewModel.uiState.value.filters.size)
        assertEquals("red", viewModel.uiState.value.filters.color)
    }

    @Test
    fun `search history is loaded from DAO`() = runTest {
        val history = listOf(
            SearchHistoryEntity(query = "sunset"),
            SearchHistoryEntity(query = "ocean")
        )
        coEvery { searchHistoryDao.getRecentSearches() } returns flowOf(history)

        viewModel = SearchViewModel(photoRepository, searchHistoryDao)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.searchHistory.size)
            assertEquals("sunset", state.searchHistory[0])
            assertEquals("ocean", state.searchHistory[1])
        }
    }
}
