package com.example.search

import com.example.search.model.GuessData
import com.example.search.model.HotSearchData
import com.example.search.model.HotSearchResponse
import com.example.search.model.SearchData
import com.example.search.model.SearchSuggestResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("search/hot/detail")
    suspend fun getSearchhot(): HotSearchResponse

    @GET("search/hot")
    suspend fun getguesshot(): GuessData

    @GET("/cloudsearch")
    suspend fun getsearchSongs(
        @Query("keywords") keywords: String,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0, //用于分页
        @Query("type") type: Int = 1      //默认 1 即单曲
    ): SearchData

    //搜索建议
    @GET("search/suggest")
    suspend fun getSearchSuggest(
        @Query("keywords") keywords: String,
    ): SearchSuggestResponse

}