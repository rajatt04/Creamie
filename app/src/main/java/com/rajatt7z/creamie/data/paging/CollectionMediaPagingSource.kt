package com.rajatt7z.creamie.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rajatt7z.creamie.data.mapper.toPhotoDomain
import com.rajatt7z.creamie.data.remote.PexelsApiService
import com.rajatt7z.creamie.domain.model.Photo

class CollectionMediaPagingSource(
    private val apiService: PexelsApiService,
    private val collectionId: String
) : PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getCollectionMedia(
                id = collectionId,
                type = "photos",
                page = page,
                perPage = params.loadSize
            )
            val photos = response.media.mapNotNull { it.toPhotoDomain() }

            LoadResult.Page(
                data = photos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.nextPage == null) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
