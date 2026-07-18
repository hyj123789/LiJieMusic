package com.example.model

import kotlinx.coroutines.flow.MutableStateFlow

object UserManager {
    val profile : MutableStateFlow<Profile?> = MutableStateFlow(null)
}

data class Profile(
    val userId: Long,           // 核心：用户ID
    val nickname: String,       // 核心：昵称
    val avatarUrl: String,      // 核心：头像
    val backgroundUrl: String?, // 核心：背景图
    val gender: Int,            // 核心：性别
    val signature: String?,     // 核心：个性签名
    val followed: Boolean,      // 核心：是否关注
    val followeds: Int,         // 核心：粉丝数
    val follows: Int,           // 核心：关注数
    val vipType: Int            // 核心：VIP状态
)