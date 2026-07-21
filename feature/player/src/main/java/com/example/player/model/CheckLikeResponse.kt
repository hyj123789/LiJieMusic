package com.example.player.model

data class CheckLikeResponse(
    val code: Int,
    //返回是红心的歌曲id
    val ids: List<Long>? = emptyList()
)