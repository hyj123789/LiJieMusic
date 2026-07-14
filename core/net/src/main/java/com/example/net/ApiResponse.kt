package com.example.net


data class ApiResponse<T>(
    val code : Int,         //api返回的code码
    val message: String?,   //接口可能返回的错误信息
    val data:T?             //服务器返回的数据
)