package com.example.mv

import retrofit2.http.GET
import retrofit2.http.Query

interface MVApi {
    @GET("video/timeline/recommend")
    suspend fun getmv(@Query("offset") offset: Int = 0): VideoRecommendResponse
}