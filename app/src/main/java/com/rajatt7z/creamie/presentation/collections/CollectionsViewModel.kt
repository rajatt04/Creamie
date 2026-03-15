package com.rajatt7z.creamie.presentation.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rajatt7z.creamie.domain.model.Collection
import com.rajatt7z.creamie.domain.repository.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    collectionRepository: CollectionRepository
) : ViewModel() {

    val collections: Flow<PagingData<Collection>> = collectionRepository
        .getPagedCollections()
        .cachedIn(viewModelScope)
}
