package com.example.player.model

/**
 * 歌曲播放链接响应
 *
 * 对应接口：GET /song/url/v1
 */
data class SongUrlResponse(
    val code: Int,
    val data: List<SongUrlData>
)

/**
 * 歌曲播放链接数据
 */
data class SongUrlData(
    val id: Int,
    val url: String?,       // 播放链接
    val time: Int,          // 时长（毫秒）
    val code: Int
)
