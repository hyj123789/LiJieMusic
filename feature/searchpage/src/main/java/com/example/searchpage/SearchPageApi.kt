package com.example.searchpage

import com.example.searchpage.model.AlbumResponse
import com.example.searchpage.model.ArtistResponse
import com.example.searchpage.model.GenreResponse
import com.example.searchpage.model.PlaylistResponse
import com.example.searchpage.model.RadioResponse
import com.example.searchpage.model.TopListResponse
import retrofit2.http.GET

interface SearchPageApi {

    @GET("top/playlist/highquality")
    suspend fun getRvPlaylist(): PlaylistResponse

    //排行第一个图标
    @GET("toplist/detail")
    suspend fun getTopList(): TopListResponse

    //歌手第2个图标
    @GET("/toplist/artist")
    suspend fun getArtist(): ArtistResponse

    //曲风第3个图标
    @GET("/style/list")
    suspend fun getGenre(): GenreResponse

    //第4个图标
    @GET("/album/new")
    suspend fun getAlbunm(): AlbumResponse

    //第4个图标
    @GET("/dj/recommend")
    suspend fun getRadio(): RadioResponse
}