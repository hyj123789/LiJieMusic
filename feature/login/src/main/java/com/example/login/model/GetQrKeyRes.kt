package com.example.login.model

data class GetQrKeyRes(
    val code: Int,
    val `data`: DataX
)
data class DataX(
    val code: Int,
    val unikey: String
)