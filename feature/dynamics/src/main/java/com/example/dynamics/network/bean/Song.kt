package com.example.dynamics.network.bean

data class Song(
    val album: Album,
    val artists: List<Artist>,
    val id: Long,
    val name: String
)