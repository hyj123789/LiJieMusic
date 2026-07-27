package com.example.playlist

import com.example.playlist.model.DeleteSongFromListRes
import com.example.playlist.model.PlaylistRes
import com.example.playlist.model.ToggleSongOrderRes
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayListApi {
    @GET("/playlist/detail")
    suspend fun getListDetail(@Query("id") id: String) : PlaylistRes
    @GET("/playlist/tracks")
    suspend fun deleteSongFromList(@Query("op") op : String ="del",
                                   @Query("pid") pid : Long,
                                   @Query("tracks") ids : String,
                                   @Query("timestamp") timestamp: Long = System.currentTimeMillis()) : DeleteSongFromListRes
    @GET("/song/order/update")
    suspend fun toggleSongOrder(@Query("pid") pid : Long,
                                @Query("ids") ids: String) : ToggleSongOrderRes
}