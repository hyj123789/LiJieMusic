package com.example.video.model

data class GetMvUrlRes(
    val code: Int,
    val `data`: Data
)
data class Data(
    val code: Int,
    val id: Int,
    val msg: String,
    val st: Int,
    val url: String
)