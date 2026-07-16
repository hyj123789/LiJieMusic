package com.example.home

import com.example.home.model.Rv1Data
import com.example.home.model.Rv2Data
import com.example.home.model.Rv3Data
import com.example.home.model.Rv4Data
import retrofit2.http.GET


interface HomeApi {
    @GET("personalized?limit=6")
    suspend fun getRv2Playlist(): Rv2Data

    @GET("top/playlist")
    suspend fun getRv1Playlists(): Rv1Data

    @GET("recommend/songs")
    suspend fun getRv3Playlists(): Rv3Data

    @GET("personalized/newsong")
    suspend fun getRv4Playlists(): Rv4Data
}