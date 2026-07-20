package com.example.dynamics.model

data class CommentThread(
    val commentCount: Int,
    val id: String,
    val likedCount: Int,
    val resourceId: Long,
    val resourceInfo: ResourceInfo,
    val resourceOwnerId: Long,
    val resourceTitle: String,
    val resourceType: Int,
    val shareCount: Int
)