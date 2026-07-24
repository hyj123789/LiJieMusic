package com.example.player.model


data class ArtistResponse(
    val code: Int,
    val message: String,
    val data: ArtistData?
)


data class ArtistData(
    val artistName: String?,   // 歌手名字
    val headPicUrl: String?,   // 照片
    val avatarPicUrl: String?, // 照片/正方形头像
    val desc: String?          // 描述
)