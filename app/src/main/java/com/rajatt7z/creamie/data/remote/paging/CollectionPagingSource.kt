package com.rajatt7z.creamie.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rajatt7z.creamie.core.common.Constants
import com.rajatt7z.creamie.data.mapper.toDomain
import com.rajatt7z.creamie.data.remote.PexelsApiService
import com.rajatt7z.creamie.domain.model.Collection
import retrofit2.HttpException
import java.io.IOException

class CollectionPagingSource(
    private val apiService: PexelsApiService
) : PagingSource<Int, Collection>() {

    override fun getRefreshKey(state: PagingState<Int, Collection>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getFeaturedCollections(
                page = page,
                perPage = Constants.DEFAULT_PAGE_SIZE
            )

            val collections = response.collections.map { it.toDomain() }

            LoadResult.Page(
                data = collections,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (collections.isEmpty() || response.nextPage == null) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}
