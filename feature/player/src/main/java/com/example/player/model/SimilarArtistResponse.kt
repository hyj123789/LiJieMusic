package com.example.player.model

data class SimilarArtistResponse(
    val code: Int?,
    val artists: List<SimilarArtist>?
)

data class SimilarArtist(
    val id: Long?,        //歌手 ID
    val name: String?,    //歌手名字
    val picUrl: String?   //歌手照片
)