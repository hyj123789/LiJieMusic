package com.example.searchpage

import retrofit2.http.GET

interface SearchPageApi {

    @GET("top/playlist/highquality")
    suspend fun getRvPlaylist(): PlaylistResponse
}