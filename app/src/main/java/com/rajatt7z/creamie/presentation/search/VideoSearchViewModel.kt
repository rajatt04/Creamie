package com.rajatt7z.creamie.presentation.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rajatt7z.creamie.core.common.Constants
import com.rajatt7z.creamie.domain.model.Video
import com.rajatt7z.creamie.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class VideoSearchFilters(
    val orientation: String? = null,
    val size: String? = null
)

data class VideoSearchUiState(
    val query: String = "",
    val filters: VideoSearchFilters = VideoSearchFilters(),
    val isFilterSheetVisible: Boolean = false
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class VideoSearchViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navQuery = savedStateHandle.get<String>("query") ?: ""

    private val _uiState = MutableStateFlow(VideoSearchUiState(query = navQuery))
    val uiState: StateFlow<VideoSearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow(navQuery)

    val searchResults: Flow<PagingData<Video>> = _searchQuery
        .debounce(Constants.SEARCH_DEBOUNCE_MS)
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            val filters = _uiState.value.filters
            videoRepository.searchVideos(
                query = query,
                orientation = filters.orientation,
                size = filters.size
            )
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        _searchQuery.value = query
    }

    fun updateFilters(filters: VideoSearchFilters) {
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
