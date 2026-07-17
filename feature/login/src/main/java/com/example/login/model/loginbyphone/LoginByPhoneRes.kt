package com.example.login.model.loginbyphone

data class LoginByPhoneRes(
    val account: Account,
    val bindings: List<Binding>,
    val code: Int,
    val cookie: String,
    val hitType: Int,
    val loginType: Int,
    val profile: Profile,
    val token: String,
    val msg: String
)