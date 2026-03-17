package com.rajatt7z.creamie.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rajatt7z.creamie.data.mapper.toDomain
import com.rajatt7z.creamie.data.remote.PexelsApiService
import com.rajatt7z.creamie.domain.model.Video

class PopularVideoPagingSource(
    private val api: PexelsApiService
) : PagingSource<Int, Video>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        val position = params.key ?: 1
        return try {
            val response = api.getPopularVideos(
                page = position,
                perPage = params.loadSize
            )
            
            val videos = response.videos.map { it.toDomain() }
            val nextKey = if (response.nextPage != null) position + 1 else null

            LoadResult.Page(
                data = videos,
                prevKey = if (position == 1) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
