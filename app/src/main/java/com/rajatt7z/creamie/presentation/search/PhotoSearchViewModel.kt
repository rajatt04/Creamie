package com.rajatt7z.creamie.presentation.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rajatt7z.creamie.core.common.Constants
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class PhotoSearchUiState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val isFilterSheetVisible: Boolean = false
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class PhotoSearchViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navQuery = savedStateHandle.get<String>("query") ?: ""

    private val _uiState = MutableStateFlow(PhotoSearchUiState(query = navQuery))
    val uiState: StateFlow<PhotoSearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow(navQuery)

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

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        _searchQuery.value = query
    }

    fun updateFilters(filters: SearchFilters) {
        _uiState.update { it.copy(filters = filters) }
        val currentQuery = _uiState.value.query
        if (currentQuery.isNotBlank()) {
            _searchQuery.value = ""
            _searchQuery.value = currentQuery
        }
    }

    fun toggleFilterSheet() {
        _uiState.update { it.copy(isFilterSheetVisible = !it.isFilterSheetVisible) }
    }
}
