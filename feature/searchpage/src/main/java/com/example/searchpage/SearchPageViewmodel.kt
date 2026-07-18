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
            //创建 Api 实例
            val api = RetrofitClient.createApi(SearchPageApi::class.java)
            //发起请求拿数据
            val response = api.getRvPlaylist()

            Log.d("hyj", "searchpageRV1 接口状态码: ${response.code}, 数据量: ${response.playlists?.size}")

            //如果成功拿到数据就放进
            if (response.code == 200) {
                _playListFlow.value = response.playlists?:emptyList()
            }
        }
    }
}