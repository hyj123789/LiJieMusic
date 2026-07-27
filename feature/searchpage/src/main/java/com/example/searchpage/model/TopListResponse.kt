package com.example.searchpage.model

data class TopListResponse(
    val code: Int,
    val list: List<TopListEntity>
)

data class TopListEntity(
    val id: Long,                 // 歌单ID (例如: 19723756)
    val name: String,             // 名字 (例如: "飙升榜")
    val coverImgUrl: String,      // 封面URL
    val description: String?,     // 描述 (注意：有些榜单可能没有描述，建议加上 ? 允许为 null)
    val updateFrequency: String   // 更新频率 (例如: "刚刚更新")
)