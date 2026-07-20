package com.example.dynamics.model

data class Song(
    val album: Album,
    val artists: List<Artist>,
    val id: Long,
    val name: String
)