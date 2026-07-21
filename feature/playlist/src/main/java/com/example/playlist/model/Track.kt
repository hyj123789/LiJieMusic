package com.example.playlist.model

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("al")
    val album: Album?,
    @SerializedName("ar")
    val artists: List<Artist>?,
    @SerializedName("dt")
    val duration: Int,
    val id: Long,
    val name: String,
    val fee: Int
)