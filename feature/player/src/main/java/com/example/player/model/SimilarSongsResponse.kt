package com.example.player.model

data class SimilarSongsResponse(
    val songs: List<Song>?,
    val code : Int
)

data class Song(
    //歌曲 ID
    val id: Long?,
    //歌名
    val name: String?,
    //歌手列表
    val artists: List<ArtistSimilar>?
)

data class ArtistSimilar(
    //歌手名
    val name: String?
)