package com.example.comment
import com.google.gson.annotations.SerializedName


data class NewCommentResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("data")
    val data: CommentResponse?
)

data class CommentResponse(

    @SerializedName("commentsTitle")
    val commentsTitle: String?,//什么全部评论哦，没有看懂

    @SerializedName("totalCount")
    val totalCount: Int = 0,//总评论数目

    @SerializedName("comments")
    val comments: List<CommentItem>? = emptyList(),

    @SerializedName("hasMore")
    val hasMore: Boolean = false, //用于判断是否还有下一页数据

    @SerializedName("cursor")
    val cursor: String? = null  //用于时间排序(sortType=3)时的下一页标识
)

//单条评论
data class CommentItem(
    //判断是不是添加进来的楼层评论
    val isreply : Boolean = false,
    //判断是否已经被拓展开了
    var isexpend : Boolean = false,

    @SerializedName("content")
    val content: String, //评论内容

    @SerializedName("timeStr")
    val timeStr: String?, //发布时间

    @SerializedName("likedCount")
    var likedCount: Int = 0, //点赞数

    @SerializedName("user")
    val user: CommentUser, //用户信息

    @SerializedName("ipLocation")
    val ipLocation: IpLocation? ,// IP属地信息（可能为空

    @SerializedName("liked")
    var liked: Boolean , //是否是红心

    //评论id
    @SerializedName("commentId")
    val commentId: Long,

    @SerializedName("replyCount")
    val replyCount: Int = 0,
)

//用户信息
data class CommentUser(
    @SerializedName("nickname")
    val nickname: String, //用户昵称

    @SerializedName("avatarUrl")
    val avatarUrl: String //用户头像链接
)

//地点
data class IpLocation(
    @SerializedName("location")
    val location: String?
)