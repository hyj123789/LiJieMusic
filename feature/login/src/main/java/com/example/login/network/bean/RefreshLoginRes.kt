package com.example.login.network.bean

data class RefreshLoginRes (
    val code: Int,
    val cookie: String
)