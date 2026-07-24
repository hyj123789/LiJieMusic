package com.example.player

import SongHistoryWikiResponse
import com.example.player.model.ArtistResponse
import com.example.player.model.CheckLikeResponse
import com.example.player.model.LikeResponse
import com.example.player.model.LyricResponse
import com.example.player.model.MusicAvailableResponse
import com.example.player.model.SimilarArtistResponse
import com.example.player.model.SimilarSongsResponse
import com.example.player.model.SongApiResponse
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

    //历史听歌记录
    @GET("/music/first/listen/info")
    suspend fun getSongListenHistory(
        @Query("id") songId: Long
    ): SongHistoryWikiResponse

    //歌曲详情
    @GET("/ugc/song/get")
    suspend fun getSongDetail2(
        @Query("id") songId: Long
    ): SongApiResponse

    //歌手的介绍/ugc/artist/get
    @GET("/ugc/artist/get")
    suspend fun getSongerDetail(
        @Query("id") songId: Long
    ): ArtistResponse

    //访问相似歌曲
    @GET("/simi/song")
    suspend fun getSimilarSongDetail(
        @Query("id") songId: Long
    ): SimilarSongsResponse


    //访问相似歌手
    @GET("/simi/artist")
    suspend fun getSimilarSongerDetail(
        @Query("id") songId: Long
    ): SimilarArtistResponse



}