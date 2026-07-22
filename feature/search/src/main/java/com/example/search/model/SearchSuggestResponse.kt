package com.example.search.model

data class SearchSuggestResponse(
    val code: Int,
    val result: SearchSuggestResult?
)

//单曲、专辑，以及排序规则
data class SearchSuggestResult(
    val songs: List<SuggestSong>?,
    val albums: List<SuggestAlbum>?,
    val order: List<String>? //["songs", "albums"]
)

data class SuggestSong(
    val id: Long,   //歌曲id
    val name: String, //歌曲名字
    val artists: List<SuggestArtist>?
)

data class SuggestAlbum(
    val id: Long,   //专辑id
    val name: String, //专辑名字
    val artist: SuggestArtist?
)

data class SuggestArtist(
    val id: Long,
    val name: String // 歌手名字
)