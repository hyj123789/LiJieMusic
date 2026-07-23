package com.example.video.model

data class GetAllMvRes(
    val code: Int,
    val count: Int,
    val `data`: List<DataX>,
    val hasMore: Boolean
)

data class Artist(
    val id: Int,
    val name: String,
    val transNames: List<String>
)

data class DataX(
    val alias: List<String>,
    val artistId: Int,
    val artistName: String,
    val artists: List<Artist>,
    val briefDesc: String,
    val cover: String,
    val duration: Int,
    val id: Int,
    val name: String,
    val playCount: Int,
    val transNames: List<String>
)