package com.rajatt7z.creamie.presentation.collections

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rajatt7z.creamie.domain.model.Photo
import com.rajatt7z.creamie.domain.repository.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CollectionDetailsViewModel @Inject constructor(
    collectionRepository: CollectionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val collectionId: String = savedStateHandle.get<String>("collectionId") ?: ""
    val collectionTitle: String = savedStateHandle.get<String>("collectionTitle") ?: "Collection"

    val media: Flow<PagingData<Photo>> = collectionRepository
        .getCollectionMedia(collectionId)
        .cachedIn(viewModelScope)
}
