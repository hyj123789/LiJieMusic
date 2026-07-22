package com.example.player

import com.example.player.model.CheckLikeResponse
import com.example.player.model.LikeResponse
import com.example.player.model.LyricResponse
import com.example.player.model.MusicAvailableResponse
import com.example.player.model.SongDetailResponse
import com.example.player.model.SongUrlResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PlayerApi {
    @GET("/check/music")
    suspend fun checkMusicIsAvailable(@Query("id") id: String): MusicAvailableResponse

    @GET("/song/url/v1")
    suspend fun getMusicUrl(
        @Query("id") id: String,
        @Query("level") level: String = "standard"
    ): SongUrlResponse

    @GET("/song/detail")
    suspend fun getSongDetail(@Query("ids") ids: String): SongDetailResponse

    @GET("/lyric/new")
    suspend fun getlyric(@Query("id") id: String): LyricResponse

    @GET("/song/like/check")
    suspend fun checkSongLike(
        @Query("ids") ids: String
    ): CheckLikeResponse

    @POST("/song/like")
    suspend fun toggleLikeSong(
        @Query("id") id: String,
        @Query("uid") uid: String,
        @Query("like") like: Boolean,
        @Query("timestamp") timestamp: Long = System.currentTimeMillis()
    ): LikeResponse
}