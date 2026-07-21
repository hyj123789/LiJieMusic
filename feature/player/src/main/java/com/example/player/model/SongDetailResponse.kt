package com.example.player.model

import com.example.base.SongDetail

/**
 * 歌曲详情响应
 *
 * 对应接口：GET /song/detail
 */
data class SongDetailResponse(
    val code: Int,
    val songs: List<SongDetail>
)