package com.example.model

import kotlinx.coroutines.flow.MutableStateFlow

object SongManager {
    private val songStatus = MutableStateFlow<SongInfo?>(null)

}

data class SongInfo(
    val id : Long,
    val listId : Long
)