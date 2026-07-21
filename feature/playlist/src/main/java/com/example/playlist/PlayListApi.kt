package com.example.playlist

import com.example.playlist.model.PlaylistRes
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayListApi {
    @GET("/playlist/detail")
    suspend fun getListDetail(@Query("id") id: String) : PlaylistRes
    @GET("/playlist/tracks")
    suspend fun deleteSongFromList(@Query("op") op : String ="del",
                                   @Query("pid") id : Long,
                                   @Query("tracks") tracks : String)
}