package com.example.searchpage.model

data class GenreResponse(
    val data: List<GenreMainTag>,
    val code : Int
)

data class GenreMainTag(
    val tagId: Long,
    val tagName: String,
    val enName: String?,
    val childrenTags: List<GenreSubTag>?
)

data class GenreSubTag(
    val tagId: Long,
    val tagName: String,
    val enName: String?,
    val picUrl: String? // 背景图片
)