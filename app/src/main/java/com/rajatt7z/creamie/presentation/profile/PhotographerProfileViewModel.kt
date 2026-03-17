package com.rajatt7z.creamie.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PhotographerProfileViewModel @Inject constructor(
    photoRepository: PhotoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val photographerName: String = savedStateHandle.get<String>("photographerName") ?: "Photographer"

    // Fetch the photographer's "portfolio" by searching their exact name on Pexels
    val portfolio: Flow<PagingData<Photo>> = photoRepository
        .searchPhotos(
            query = photographerName,
            color = null,
            orientation = null,
            size = null
        )
        .cachedIn(viewModelScope)
}
