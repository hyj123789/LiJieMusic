package com.example.search.model

data class SearchData(
    val code: Int,
    val result: SearchResultData?
)

data class SearchResultData(
    val songs: List<SongItem>?
)

data class SongItem(
    val id: Long,
    val name: String,         // 歌曲名
    val ar: List<Artist>?, // 歌手列表
    val al: Album?         // 专辑信息
)

data class Artist(
    val id: Long,
    val name: String,
)

data class Album(
    val id: Long,
    val name: String,
    val picUrl: String?
)