package com.example.comment

import retrofit2.http.GET
import retrofit2.http.Query

interface CommentApi {

//    @GET("/comment/music")
//    suspend fun getComment(
//        @Query("id") songId: String,
//        @Query("limit") limit: Int = 20,
//        @Query("offset") offset: Int = 0
//    ): CommentResponse


    @GET("/comment/new")
    suspend fun getNewComments(
        @Query("id") id: String,
        @Query("type") type: Int = 0, //查歌曲默认是0
        @Query("sortType") sortType: Int = 1,//推荐、热度、时间：1，2，3
        @Query("pageNo") pageNo: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("cursor") cursor: String? = null
    ): NewCommentResponse

    @GET("/comment/floor")
    suspend fun getFloorComments(
        //必选参数
        @Query("parentCommentId") parentCommentId: Long, //楼主的评论 ID
        @Query("id") songId: String,                     //歌曲 ID
        @Query("type") type: Int = 0,                    //资源类型：0

        @Query("limit") limit: Int = 10,                 //每次获取几条，默认 20
        @Query("time") time: Long? = null
    ): FloorCommentResponse


}