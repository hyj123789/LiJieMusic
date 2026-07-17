package com.example.login.model

data class GuestLoginRes(
    val code: Int,
    val cookie: String,
    val createTime: Long,
    val userId: Long
)