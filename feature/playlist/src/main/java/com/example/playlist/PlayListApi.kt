package com.example.playlist

import com.example.playlist.model.CoverUpdateRes
import com.example.playlist.model.DeleteSongFromListRes
import com.example.playlist.model.PlaylistRes
import com.example.playlist.model.ToggleSongOrderRes
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
    @Multipart
    @POST("/playlist/cover/update")
    suspend fun updateCover(
        @Query("id") id: Long,
        @Part imgFile: MultipartBody.Part,
        @Query("imgSize") imgSize: Int = 300,
        @Query("imgX") imgX: Int = 0,
        @Query("imgY") imgY: Int = 0
    ): CoverUpdateRes
}