package com.example.home.model

data class Rv4Data(
    val code: Int,
    val category: Int,
    val result: List<Rv4Item>? //数据列表
)

data class Rv4Item(
    val id: Long,
    val name: String,
    val picUrl: String, //封面图 URL
    val song: Rv4Song?
)

// 3. 嵌套的 song 对象
data class Rv4Song(
    val id: Long,
    val name: String, //歌曲名字
    val artists: List<Rv4Artist>? //歌手列表
)

data class Rv4Artist(
    val id: Long,
    val name: String //歌手名字
)