package com.example.video.model

data class GetTopMvRes(
    val code: Int,
    val `data`: List<DataTop>,
    val hasMore: Boolean,
)

data class DataTop(
    val artistId: Int,
    val artistName: String,
    val artists: List<Artist>,
    val briefDesc: String?,
    val cover: String,
    val desc: String?,
    val duration: Int,
    val id: Int,
    val lastRank: Int,
    val mark: Int,
    val mv: Mv,
    val name: String,
    val playCount: Int,
    val score: Int,
    val subed: Boolean
)

data class Mv(
    val aliaName: String,
    val appTitle: String,
    val appword: String,
    val area: String,
    val artists: List<Artist>,
    val authId: Int,
    val caption: Int,
    val captionLanguage: String,
    val dayplays: Int,
    val desc: String,
    val fee: Int,
    val id: Int,
    val monthplays: Int,
    val mottos: String,
    val neteaseonly: Int,
    val oneword: Any,
    val online: Long,
    val pic16v9: Long,
    val pic4v3: Long,
    val plays: Int,
    val publishTime: String,
    val score: Int,
    val stars: Any,
    val status: Int,
    val style: Any,
    val subTitle: String,
    val subType: String,
    val title: String,
    val topWeeks: String,
    val transName: String,
    val type: String,
    val upban: Int,
    val valid: Int,
    val videos: List<Video>,
    val weekplays: Int
)

data class TagSign(
    val br: Int,
    val mvtype: String,
    val resolution: Int,
    val tagSign: String,
    val type: String
)

data class Video(
    val check: Boolean,
    val container: String,
    val duration: Int,
    val height: Int,
    val md5: String,
    val size: Int,
    val tag: String,
    val tagSign: TagSign,
    val url: String,
    val width: Int
)