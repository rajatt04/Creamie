package com.rajatt7z.creamie.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rajatt7z.creamie.data.mapper.toDomain
import com.rajatt7z.creamie.data.remote.PexelsApiService
import com.rajatt7z.creamie.domain.model.Photo

class SearchPagingSource(
    private val apiService: PexelsApiService,
    private val query: String,
    private val orientation: String? = null,
    private val size: String? = null,
    private val color: String? = null,
    private val locale: String? = null
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
            val response = apiService.searchPhotos(
                query = query,
                orientation = orientation,
                size = size,
                color = color,
                locale = locale,
                page = page,
                perPage = params.loadSize
            )
            val photos = response.photos.map { it.toDomain() }

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
