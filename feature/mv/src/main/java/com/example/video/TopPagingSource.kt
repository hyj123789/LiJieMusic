package com.example.video

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.video.model.DataTop

class TopPagingSource(private val api: MvApi,
    private  val area  : String) : PagingSource<Int, DataTop>(){
    override fun getRefreshKey(state: PagingState<Int, DataTop>): Int? {
        return state.anchorPosition?.let { anchorPosition->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataTop> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val offset = (page -1)*pageSize
            if (area == "全部"){
                val response = api.getTopMv(offset=offset)
                LoadResult.Page(data = response.data,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (response.hasMore) page + 1 else null)
            } else{
                val topMvNormal = api.getTopMvNormal(area = area,offset=offset)
                LoadResult.Page(data = topMvNormal.data,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (topMvNormal.hasMore) page + 1 else null)
            }
        } catch (e : Exception){
            LoadResult.Error(e)
        }
    }
}