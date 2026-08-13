package com.example.profile.network.bean

data class GetPlaylistRes(
    val code: Int,
    val more: Boolean,
    val playlist: List<Playlist>
)