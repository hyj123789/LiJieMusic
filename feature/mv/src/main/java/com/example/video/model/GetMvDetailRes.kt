package com.example.video.model

data class GetMvDetailRes(
    val code: Int,
    val commentCount: Int,
    val liked: Boolean,
    val likedCount: Int,
    val shareCount: Int
)