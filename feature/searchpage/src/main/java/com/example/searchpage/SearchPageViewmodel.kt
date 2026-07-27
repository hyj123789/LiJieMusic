package com.example.searchpage

import android.util.Log
import com.example.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.net.RetrofitClient

class SearchPageViewmodel : BaseViewModel() {

    private val _playListFlow = MutableStateFlow<List<PlaylistItem>>(emptyList())
    val playListFlow: StateFlow<List<PlaylistItem>> get() = _playListFlow


    fun fetchRecommendPlaylists() {
        launchRequest {
            try {
                val api = RetrofitClient.createApi(SearchPageApi::class.java)
                val response = api.getRvPlaylist()
                Log.d("hyj", "searchpageRV1 接口状态码: ${response.code}, 数据量: ${response.playlists?.size}")
                if (response.code == 200) {
                    _playListFlow.value = response.playlists ?: emptyList()
                } else {
                    Log.w("hyj", "接口请求成功但业务失败，状态码: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("hyj", "获取推荐歌单失败: ${e.message}", e)
                _playListFlow.value = emptyList()
            }
        }
    }
}