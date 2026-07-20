package com.example.dynamics

import com.example.dynamics.model.DynamicsRes
import retrofit2.http.GET
import retrofit2.http.Query

interface DynamicsApi {
    @GET("/event")
    suspend fun getFriendsDynamics(@Query("pagesize") pagesize : Int = 20,
                                   @Query("lasttime") lasttime : Long = -1) : DynamicsRes
}