package com.rajatt7z.creamie.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rajatt7z.creamie.core.network.NetworkResult
import com.rajatt7z.creamie.domain.model.Collection
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.repository.CollectionRepository
import com.rajatt7z.creamie.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val featuredCollections: List<Collection> = emptyList(),
    val isCollectionsLoading: Boolean = true,
    val collectionsError: String? = null,
    val trendingSearches: List<String> = listOf(
        "Nature", "Dark", "Abstract", "Minimal", "Space",
        "Ocean", "Mountain", "Neon", "Flowers", "City"
    )
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    val curatedPhotos: Flow<PagingData<Photo>> = photoRepository
        .getCuratedPhotos()
        .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFeaturedCollections()
    }

    fun loadFeaturedCollections() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCollectionsLoading = true, collectionsError = null) }
            when (val result = collectionRepository.getFeaturedCollections()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            featuredCollections = result.data,
                            isCollectionsLoading = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isCollectionsLoading = false,
                            collectionsError = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
