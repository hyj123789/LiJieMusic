package com.example.dynamics.model

data class DynamicsRes(
    val code: Int,
    val event: List<Event>,
    val more: Boolean
)