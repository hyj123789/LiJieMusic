package com.example.searchpage.model

// 1. 最外层响应
data class AlbumResponse(
    val total: Int,
    val code : Int,
    val albums: List<AlbumEntity>
)

// 2. 具体的专辑实体
data class AlbumEntity(
    val id: Long,               // 专辑 ID
    val name: String,           // 专辑名字 (例如: "YOUNGEST")
    val picUrl: String,         // 封面图片 URL (已经是完整的 https 地址了，可以直接用！)
    val artist: AlbumArtist?    // 歌手信息（如果想在副标题显示歌手名的话）
)

data class AlbumArtist(
    val id: Long,
    val name: String
)