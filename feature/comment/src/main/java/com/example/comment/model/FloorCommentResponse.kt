package com.example.comment.model

import com.google.gson.annotations.SerializedName

data class FloorCommentResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: FloorData
)
data class FloorData(
    @SerializedName("ownerComment")
    val ownerComment: CommentItem,
    @SerializedName("comments")
    val comments: List<CommentItem>?
)