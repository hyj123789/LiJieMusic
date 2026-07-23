package com.example.video

import com.example.video.model.GetAllMvRes
import com.example.video.model.GetMvDetailRes
import com.example.video.model.GetMvUrlRes
import com.example.video.model.GetTopMvRes
import com.example.video.model.VideoRecommendResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MvApi {
    @GET("video/timeline/recommend")
    suspend fun getRecommendMv(@Query("offset") offset: Int = 0): VideoRecommendResponse
    @GET("/mv/url")
    suspend fun getMvUrl(@Query("id") id : Long) : GetMvUrlRes
    @GET("/mv/all")
    suspend fun getAllMv(@Query("area") area: String = "全部",
                         @Query("type") type: String = "全部",
                         @Query("offset") offset: Int = 0 ) : GetAllMvRes
    @GET("/mv/detail/info")
    suspend fun getMvDetail(@Query("mvid") id: Long) : GetMvDetailRes
    @GET("/top/mv")
    suspend fun getTopMvNormal(@Query("limit") limit: Int = 30,
                         @Query("area") area: String = "港台",
                         @Query("offset") offset: Int = 0) : GetTopMvRes
    @GET("/top/mv")
    suspend fun getTopMv(@Query("limit") limit: Int = 30,
                         @Query("offset") offset: Int = 0) : GetTopMvRes
}