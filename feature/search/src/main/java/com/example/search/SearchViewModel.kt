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


    private val _SearchFlowSuggest = MutableStateFlow<List<String>>(emptyList())

    val SearchFlowSuggest : StateFlow<List<String>> get() = _SearchFlowSuggest

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

            Log.d("hyj", "Search 接口状态码:${response3.code} ,数据量${response3.result?.songs?.size}")
            Log.d("hyj", "服务器真实返回的JSON: ${response3}")

            if (response3.code == 200) {
                _SearchFlow.value = response3.result?.songs ?: emptyList()
            }
        }
    }

    fun fetchSearchSuggestion(keyword: String) {
        launchRequest{
            try {
                //发起网络请求
                val api = RetrofitClient.createApi(SearchApi::class.java)
                val response = api.getSearchSuggest(keywords = keyword)
                Log.d("hyj", "服务器原始返回: $response")

                //检查有没有成功拿到数据
                if (response.code == 200 && response.result != null) {
                    val result = response.result

                    //把拿到的歌曲和专辑提取出来
                    val songsCount = result.songs?.size ?: 0
                    val albumsCount = result.albums?.size ?: 0

                    Log.d("hyj", "成功拿到提示！包含 $songsCount 首歌，和 $albumsCount 张专辑")

                    val suggestList = mutableListOf<String>()

                    result.order?.forEach { type ->
                        when (type) {
                            "songs" -> {
                                result.songs?.forEach { song ->
                                    val singer = song.artists?.firstOrNull()?.name ?: "未知歌手"
                                    suggestList.add("歌曲：${song.name} - $singer")
                                }
                            }
                            "albums" -> {
                                result.albums?.forEach { album ->
                                    val singer = album.artist?.name ?: "未知歌手"
                                    suggestList.add("专辑：${album.name} - $singer")
                                }
                            }
                        }
                    }
                    //上传数据
                    _SearchFlowSuggest.value = suggestList

                } else {
                    Log.e("hyj", "请求成功，但数据是空的")
                }
            } catch (e: Exception) {
                // 捕获网络异常（比如没网了）
                Log.e("hyj", "网络请求崩溃啦: ${e.message}")
            }
        }
    }


}