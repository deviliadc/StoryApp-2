package com.devapps.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.devapps.storyapp.data.api.ApiService
import com.devapps.storyapp.data.model.Story

class StoryPagingSource(
    private val token: String,
    private val apiService: ApiService
) : PagingSource<Int, Story>() {

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val response = if (token.isNotEmpty()) {
                apiService.getStories(
                    "Bearer $token",
                    page,
                    params.loadSize)
            } else {
                return LoadResult.Error(Exception("Token Tidak Ada"))
            }

            return if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                LoadResult.Page(
                    data = stories,
                    prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                    nextKey = if (stories.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("Gagal Memuat List Story"))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
