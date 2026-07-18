package com.example.profile.model.playlist

data class GetPlaylistRes(
    val code: Int,
    val more: Boolean,
    val playlist: List<Playlist>
)