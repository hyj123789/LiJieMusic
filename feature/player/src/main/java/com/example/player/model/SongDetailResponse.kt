package com.example.player.model

/**
 * 歌曲详情响应
 *
 * 对应接口：GET /song/detail
 */
data class SongDetailResponse(
    val code: Int,
    val songs: List<SongDetail>
)

/**
 * 歌曲详情
 */
data class SongDetail(
    val id: Long,
    val name: String,
    val ar: List<Artist>,
    val al: Album,
    val dt: Int,    // 时长（毫秒）
    val fee: Int    // 是否需要 VIP
)

/**
 * 歌手信息
 */
data class Artist(
    val id: Long,
    val name: String
)

/**
 * 专辑信息
 */
data class Album(
    val id: Long,
    val name: String,
    val picUrl: String  //封面图片 URL
)