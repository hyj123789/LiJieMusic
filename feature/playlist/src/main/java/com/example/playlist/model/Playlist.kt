package com.example.playlist.model

data class Playlist(
    val coverImgUrl: String,
    val creator: Creator,
    val description: String,
    val id: Long,
    val name: String,
    val playCount: Int,
    val trackCount: Int,
    val tracks: List<Track>
)