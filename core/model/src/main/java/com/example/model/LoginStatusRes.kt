package com.example.model

data class LoginStatusRes(val `data`: Data)

data class Data(
    val account: Account,
    val code: Int,
    val profile: Profile
)
data class Account(
    val anonimousUser: Boolean,
    val ban: Int,
    val baoyueVersion: Int,
    val createTime: Long,
    val donateVersion: Int,
    val id: Long,
    val paidFee: Boolean,
    val status: Int,
    val tokenVersion: Int,
    val type: Int,
    val userName: String,
    val vipType: Int,
    val whitelistAuthority: Int
)