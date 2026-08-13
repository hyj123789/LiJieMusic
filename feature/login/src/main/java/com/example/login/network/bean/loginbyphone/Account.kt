package com.example.login.network.bean.loginbyphone

data class Account(
    val anonimousUser: Boolean,
    val ban: Int,
    val baoyueVersion: Int,
    val createTime: Long,
    val donateVersion: Int,
    val id: Long,
    val salt: String,
    val status: Int,
    val tokenVersion: Int,
    val type: Int,
    val uninitialized: Boolean,
    val userName: String,
    val vipType: Int,
    val viptypeVersion: Long,
    val whitelistAuthority: Int
)