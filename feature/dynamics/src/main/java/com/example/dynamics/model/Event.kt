package com.example.dynamics.model

import com.google.gson.annotations.JsonAdapter

data class Event(
    val actName: String,
    val canShare: Boolean,
    val discussId: String,
    val encryptUserId: String,
    val eventTime: Long,
    val forwardCount: Int,
    val id: Long,
    val info: Info,
    val insiteForwardCount: Int,
    val ipLocation: IpLocation,
    @JsonAdapter(JsonDataDeserializer::class)
    val json: JsonData,
    val pics: List<Pic>,
    val pubSource: PubSource,
    val showTime: Long,
    val type: Int,
    val user: User
)