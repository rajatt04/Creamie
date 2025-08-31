package com.rajatt7z.creamie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajatt7z.creamie.api.Photo
import com.rajatt7z.creamie.repository.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val photos: List<Photo> = emptyList(),
    val error: String? = null,
    val searchResults: List<Photo> = emptyList(),
    val isSearchLoading: Boolean = false,
    val searchError: String? = null
)

class DashboardViewModel(
    private val repository: PhotoRepository = PhotoRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Cache for tab photos
    private val tabPhotosCache = mutableMapOf<String, List<Photo>>()

    fun loadPhotosForTab(query: String) {
        // Check cache first
        tabPhotosCache[query]?.let { cachedPhotos ->
            _uiState.value = _uiState.value.copy(
                photos = cachedPhotos,
                isLoading = false,
                error = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.searchPhotos(query, 15).fold(
                onSuccess = { photos ->
                    // Cache the results
                    tabPhotosCache[query] = photos

                    _uiState.value = _uiState.value.copy(
                        photos = photos,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        photos = emptyList(),
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    fun searchPhotos(query: String) {
        if (query.trim().isEmpty()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                isSearchLoading = false,
                searchError = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearchLoading = true, searchError = null)

            repository.searchPhotos(query.trim(), 20).fold(
                onSuccess = { photos ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = photos,
                        isSearchLoading = false,
                        searchError = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = emptyList(),
                        isSearchLoading = false,
                        searchError = exception.message ?: "Search failed"
                    )
                }
            )
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            isSearchLoading = false,
            searchError = null
        )
    }

    fun refreshCurrentTab(query: String) {
        // Clear cache for this tab and reload
        tabPhotosCache.remove(query)
        loadPhotosForTab(query)
    }
}