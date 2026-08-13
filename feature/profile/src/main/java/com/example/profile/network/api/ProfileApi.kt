package com.example.profile.network.api

import com.example.profile.network.bean.GetPlaylistRes
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApi {
    @GET("/user/playlist")
    suspend fun getPlayList(@Query("uid") uid: String): GetPlaylistRes
}