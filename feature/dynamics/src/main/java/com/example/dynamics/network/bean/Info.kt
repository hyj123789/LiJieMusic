package com.example.dynamics.network.bean

data class Info(
    val commentCount: Int,
    val commentThread: CommentThread,
    val liked: Boolean,
    val likedCount: Int,
    val resourceId: Long,
    val resourceType: Int,
    val shareCount: Int,
    val threadId: String
)