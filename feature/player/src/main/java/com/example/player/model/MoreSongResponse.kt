package com.example.player.model


data class MoreSongResponse(
    val songs: List<MoreSongItem>?,
    val code : Int,
)

data class MoreSongItem(
    val id: Long?,       // 歌曲 ID
    val name: String?, // 歌名
    val ar : List<SingData>?
)
data class SingData(
    val name: String?,
    val id: Long?
)