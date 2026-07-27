package com.example.searchpage.model

data class RadioResponse(
    val djRadios: List<RadioEntity>,
    val code : Int
)

data class RadioEntity(
    val id: Long,
    val name: String,           // 播客名字 (例如: "你，静不下来")
    val picUrl: String,         // 播客封面 URL (已经是完整的 https 链接)
    val rcmsdtext: String?,     // 推荐文案 (可选)
    val dj: RadioDj?            // 主播信息
)

data class RadioDj(
    val id: Long,
    val nickname: String        // 作者/主播名字 (例如: "李静-LIJING")
)