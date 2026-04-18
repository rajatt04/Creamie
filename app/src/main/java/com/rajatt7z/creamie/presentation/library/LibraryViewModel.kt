package com.rajatt7z.creamie.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajatt7z.creamie.data.local.dao.DownloadHistoryDao
import com.rajatt7z.creamie.data.local.entity.DownloadHistoryEntity
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.rajatt7z.creamie.domain.model.FollowedPhotographer
import com.rajatt7z.creamie.domain.repository.FollowsRepository

data class LibraryUiState(
    val selectedTab: Int = 0,
    val favorites: List<Photo> = emptyList(),
    val downloads: List<DownloadHistoryEntity> = emptyList(),
    val follows: List<FollowedPhotographer> = emptyList()
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val downloadHistoryDao: DownloadHistoryDao,
    private val followsRepository: FollowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            favoritesRepository.getAllFavorites().collect { favs ->
                _uiState.update { it.copy(favorites = favs) }
            }
        }
        viewModelScope.launch {
            downloadHistoryDao.getAllDownloads().collect { downloads ->
                _uiState.update { it.copy(downloads = downloads) }
            }
        }
        viewModelScope.launch {
            followsRepository.getAllFollows().collect { f ->
                _uiState.update { it.copy(follows = f) }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun clearDownloadHistory() {
        viewModelScope.launch {
            downloadHistoryDao.clearAll()
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            favoritesRepository.clearAll()
        }
    }
}
