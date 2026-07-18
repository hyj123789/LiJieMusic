package com.example.profile

import com.example.profile.model.playlist.GetPlaylistRes
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApi {
    @GET("/user/playlist")
    suspend fun getPlayList(@Query("uid") uid: String): GetPlaylistRes
}