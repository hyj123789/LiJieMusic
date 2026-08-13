package com.example.dynamics.network.bean

data class DynamicsRes(
    val code: Int,
    val event: List<Event>,
    val more: Boolean
)