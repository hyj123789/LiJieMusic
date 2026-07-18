package com.example.login.model

import okhttp3.Cookie

data class RefreshLoginRes (
    val code: Int,
    val cookie: String
)