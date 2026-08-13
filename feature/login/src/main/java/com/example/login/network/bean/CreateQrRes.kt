package com.example.login.network.bean

data class CreateQrRes(
    val code: Int,
    val `data`: Data
)
data class Data(
    val qrurl: String,
    val qrimg: String
)