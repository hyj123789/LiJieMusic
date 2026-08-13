package com.example.dynamics.network.api

import com.example.dynamics.network.bean.DynamicsRes
import retrofit2.http.GET
import retrofit2.http.Query

interface DynamicsApi {
    @GET("/event")
    suspend fun getFriendsDynamics(@Query("pagesize") pagesize : Int = 20,
                                   @Query("lasttime") lasttime : Long = -1) : DynamicsRes
}