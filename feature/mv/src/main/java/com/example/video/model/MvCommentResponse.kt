package com.example.video.model

import android.hardware.camera2.TotalCaptureResult


data class MvCommentResponse(
    val userId: Long,
    val hotComments: List<HotComment>?,
    val total : Int
)

data class HotComment(
    val user: User?,
    val commentId: Long,
    val content: String?,
    val timeStr: String?,
    val likedCount: Int,
    var liked: Boolean
)

data class User(
    val avatarUrl: String?,
    val nickname: String?,
    val avatarDetail: Any?,
    val userId: Long,
)

data class IpLocation(
    val ip: String?,
    val location: String?,
    val userId: Long?
)