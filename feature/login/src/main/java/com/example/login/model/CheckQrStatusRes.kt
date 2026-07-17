package com.example.login.model

data class CheckQrStatusRes(
    val code: Int,
    val cookie: String,
    val message: String
)