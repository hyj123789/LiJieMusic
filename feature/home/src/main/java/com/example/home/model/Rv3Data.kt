package com.example.home.model

data class Rv3Data(
    val code: Int,
    val data: DailyData
)

//data里的内容
data class DailyData(
    val dailySongs: List<SongItem>
)

//具体的每首歌
data class SongItem(
    val id: Long,
    val name: String,     //歌名
    val ar: List<Artist>, // 歌手列表
    val al: Album         //专辑信息(里面藏着封面)
)

//歌手
data class Artist(
    val name: String      //歌手名
)

//专辑
data class Album(
    val picUrl: String    //歌曲封面图
)

