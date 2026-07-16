package com.example.home

import android.util.Log
import com.example.base.BaseViewModel
import com.example.home.model.DailyData
import com.example.home.model.PlaylistInfo1
import com.example.home.model.PlaylistInfo2
import com.example.home.model.Rv4Item
import com.example.home.model.SongItem
import com.example.net.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : BaseViewModel() {
    private val _playListFlow2 = MutableStateFlow<List<PlaylistInfo2>>(emptyList())

    val playlistFlow2 : StateFlow<List<PlaylistInfo2>> get() = _playListFlow2

    private val _playListFlow1 = MutableStateFlow<List<PlaylistInfo1>>(emptyList())

    val playlistFlow1 : StateFlow<List<PlaylistInfo1>> get() = _playListFlow1

    private val _playlistFlow3 = MutableStateFlow<List<SongItem>>(emptyList())
    val playlistFlow3: StateFlow<List<SongItem>> get() = _playlistFlow3

    private val _playlistFlow4 = MutableStateFlow<List<Rv4Item>>(emptyList())
    val playlistFlow4: StateFlow<List<Rv4Item>> get() = _playlistFlow4

    fun fetchRecommendPlaylists() {
        launchRequest {
            //创建 Api 实例
            val api = RetrofitClient.createApi(HomeApi::class.java)
            //发起请求拿数据
            val response2 = api.getRv2Playlist()
            val response1 = api.getRv1Playlists()
            val response3 = api.getRv3Playlists()
            val response4 = api.getRv4Playlists()

            Log.d("请求排查", "RV1 接口状态码: ${response1.code}, 数据量: ${response1.playlists?.size}")
            Log.d("请求排查", "RV2 接口状态码: ${response2.code}, 数据量: ${response2.result?.size}")
            Log.d("请求排查", "RV3 接口状态码: ${response3.code}, 数据量: ${response3.data?.dailySongs?.size}")
            Log.d("请求排查", "RV4 接口状态码: ${response4.code}, 数据量: ${response4.result?.size}")

            //如果成功拿到数据就放进
            if (response2.code == 200) {
                _playListFlow2.value = response2.result
            }

            if (response1.code == 200){
                _playListFlow1.value = response1.playlists
            }

            if (response3.code == 200){
                _playlistFlow3.value = response3.data.dailySongs
            }

            if (response4.code == 200){
                _playlistFlow4.value = response4.result?:emptyList()
            }
        }
    }


}