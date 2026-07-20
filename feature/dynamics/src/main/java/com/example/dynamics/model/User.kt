package com.example.dynamics.model

data class User(
    val avatarUrl: String,
    val followed: Boolean,
    val gender: Int,
    val nickname: String,
    val userId: Long,
    val vipType: Int
)