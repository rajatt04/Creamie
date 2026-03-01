package com.rajatt7z.creamie.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rajatt7z.creamie.core.common.Constants
import com.rajatt7z.creamie.data.local.dao.SearchHistoryDao
import com.rajatt7z.creamie.data.local.entity.SearchHistoryEntity
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchFilters(
    val orientation: String? = null,
    val size: String? = null,
    val color: String? = null,
    val locale: String? = null
)

data class SearchUiState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val isFilterSheetVisible: Boolean = false,
    val searchHistory: List<String> = emptyList()
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val searchHistoryDao: SearchHistoryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    val searchResults: Flow<PagingData<Photo>> = _searchQuery
        .debounce(Constants.SEARCH_DEBOUNCE_MS)
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            val filters = _uiState.value.filters
            photoRepository.searchPhotos(
                query = query,
                orientation = filters.orientation,
                size = filters.size,
                color = filters.color,
                locale = filters.locale
            )
        }
        .cachedIn(viewModelScope)

    init {
        // Load search history
        viewModelScope.launch {
            searchHistoryDao.getRecentSearches().collect { history ->
                _uiState.update { it.copy(searchHistory = history.map { h -> h.query }) }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        _searchQuery.value = query
    }

    fun onSearch(query: String) {
        if (query.isBlank()) return
        _uiState.update { it.copy(query = query) }
        _searchQuery.value = query
        viewModelScope.launch {
            searchHistoryDao.insertSearch(SearchHistoryEntity(query = query.trim()))
        }
    }

    fun onHistoryItemClick(query: String) {
        onSearch(query)
    }

    fun onDeleteHistory(query: String) {
        viewModelScope.launch {
            searchHistoryDao.deleteSearch(query)
        }
    }

    fun onClearAllHistory() {
        viewModelScope.launch {
            searchHistoryDao.clearAll()
        }
    }

    fun updateFilters(filters: SearchFilters) {
        _uiState.update { it.copy(filters = filters) }
        // Re-trigger search with new filters
        val currentQuery = _uiState.value.query
        if (currentQuery.isNotBlank()) {
            _searchQuery.value = "" // Reset
            _searchQuery.value = currentQuery // Re-emit
        }
    }

    fun toggleFilterSheet() {
        _uiState.update { it.copy(isFilterSheetVisible = !it.isFilterSheetVisible) }
    }
}
