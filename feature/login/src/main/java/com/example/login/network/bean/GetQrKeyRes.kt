package com.example.login.network.bean

data class GetQrKeyRes(
    val code: Int,
    val `data`: DataX
)
data class DataX(
    val code: Int,
    val unikey: String
)