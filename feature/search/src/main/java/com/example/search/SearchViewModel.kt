package com.example.search

import android.util.Log
import com.example.base.BaseViewModel
import com.example.net.RetrofitClient
import com.example.search.model.HotSearchData
import com.example.search.model.HotSearchItem
import com.example.search.model.HotSearchResponse
import com.example.search.model.SongItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchViewModel : BaseViewModel() {
    private val _hotSearchFlow = MutableStateFlow<List<HotSearchData>>(emptyList())

    val hotSearchFlow : StateFlow<List<HotSearchData>> get() = _hotSearchFlow

    private val _GuessFlow = MutableStateFlow<List<HotSearchItem>>(emptyList())

    val GuessFlow : StateFlow<List<HotSearchItem>> get() = _GuessFlow

    private val _SearchFlow = MutableStateFlow<List<SongItem>>(emptyList())

    val SearchFlow : StateFlow<List<SongItem>> get() = _SearchFlow

    fun fetchRecommendPlaylists() {
        launchRequest {
            //创建 Api 实例
            val api = RetrofitClient.createApi(SearchApi::class.java)
            //发起请求拿数据
            val response2 = api.getSearchhot()
            val response1 = api.getguesshot()



            Log.d("hyj", "Guess 接口状态码: ${response1.code}, 数据量: ${response1.result?.hots?.size}")
            Log.d("hyj", "Hotsearch 接口状态码: ${response2.code}, 数据量: ${response2.data?.size}")

            //如果成功拿到数据就放进
            if (response2.code == 200) {
                _hotSearchFlow.value = response2.data ?: emptyList()
            }

            if (response1.code == 200){
                _GuessFlow.value = response1.result?.hots?:emptyList()
            }
        }
    }

    fun searchit(keyword: String){
        launchRequest {
            val api = RetrofitClient.createApi(SearchApi::class.java)

            val response3 = api.getsearchSongs(keyword, 30, 1)

            Log.d("请求排查", "Search 接口状态码:${response3.code} ,数据量${response3.result?.songs?.size}")
            Log.d("请求排查", "服务器真实返回的JSON: ${response3}")

            if (response3.code == 200) {
                _SearchFlow.value = response3.result?.songs ?: emptyList()
            }
        }
    }


}