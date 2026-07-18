package com.example.model

import kotlinx.coroutines.flow.MutableStateFlow

object UserManager {
    val profile : MutableStateFlow<Profile?> = MutableStateFlow(null)
    val account: MutableStateFlow<Account?> = MutableStateFlow(null)
}

data class Profile(
    val userId: Long,
    val nickname: String,
    val avatarUrl: String,
    val backgroundUrl: String?,
    val gender: Int,
    val signature: String?,
    val followed: Boolean,
    val followeds: Int,
    val follows: Int,
    val vipType: Int
)