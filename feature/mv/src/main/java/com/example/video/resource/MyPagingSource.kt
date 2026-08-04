package com.example.video.resource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.video.MvApi
import com.example.video.model.DataX

class MyPagingSource(
    private val api: MvApi,
    private val area: String = "全部",
    private val type: String = "全部"
) : PagingSource<Int, DataX>() {

    override fun getRefreshKey(state: PagingState<Int, DataX>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataX> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val offset = (page - 1) * pageSize

            val response = api.getAllMv(area, type, offset)

            LoadResult.Page(
                data = response.data,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (response.hasMore) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}