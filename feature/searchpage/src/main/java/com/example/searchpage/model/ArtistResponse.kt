package com.example.searchpage.model

data class ArtistResponse(
    val list: ArtistListWrapper,
    val code : Int
)

data class ArtistListWrapper(
    val artists: List<ArtistEntity>
)

data class ArtistEntity(
    val id: Long,               // 歌手ID (例如: 3684)
    val name: String,           // 名字 (例如: "林俊杰")
    val picUrl: String          // 封面图片URL
)