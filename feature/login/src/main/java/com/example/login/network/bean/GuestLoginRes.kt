package com.example.login.network.bean

data class GuestLoginRes(
    val code: Int,
    val cookie: String,
    val createTime: Long,
    val userId: Long
)