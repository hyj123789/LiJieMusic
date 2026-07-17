package com.example.login.model

data class CreateQrRes(
    val code: Int,
    val `data`: Data
)
data class Data(
    val qrurl: String
)