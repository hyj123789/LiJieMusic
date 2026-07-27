package com.example.searchpage

import android.util.Log
import com.example.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.net.RetrofitClient
import com.example.searchpage.model.AlbumEntity
import com.example.searchpage.model.ArtistEntity
import com.example.searchpage.model.GenreSubTag
import com.example.searchpage.model.PlaylistItem
import com.example.searchpage.model.RadioEntity
import com.example.searchpage.model.TopListEntity
import kotlinx.coroutines.flow.asStateFlow

class SearchPageViewmodel : BaseViewModel() {

    private val _playListFlow = MutableStateFlow<List<PlaylistItem>>(emptyList())
    val playListFlow: StateFlow<List<PlaylistItem>> get() = _playListFlow

    //歌曲
    private val _topListFlow = MutableStateFlow<List<TopListEntity>>(emptyList())
    val topList = _topListFlow.asStateFlow()

    //歌手
    private val _artistFlow = MutableStateFlow<List<ArtistEntity>>(emptyList())
    val artist = _artistFlow.asStateFlow()

    //曲风
    private val _genreFlow = MutableStateFlow<List<GenreSubTag>>(emptyList())
    val genre = _genreFlow.asStateFlow()

    //新碟
    private val _albumFlow = MutableStateFlow<List<AlbumEntity>>(emptyList())
    val album = _albumFlow.asStateFlow()

    //广播
    private val _radioFlow = MutableStateFlow<List<RadioEntity>>(emptyList())
    val radioFlow = _radioFlow.asStateFlow()

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

    fun fetchTopList() {
        launchRequest {
            try {
                val api = RetrofitClient.createApi(SearchPageApi::class.java)
                val response = api.getTopList()
                Log.d("hyj", "排行榜接口状态码: ${response.code}, 数据量: ${response?.list?.size}")
                if (response.code == 200) {
                    _topListFlow.value = response.list
                } else {
                    Log.w("hyj", "排行榜接口请求成功但业务失败，状态码: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("hyj", "获取推荐歌单失败: ${e.message}", e)
                _topListFlow.value = emptyList()
            }
        }
    }

    fun fetchArtist() {
        launchRequest {
            try {
                val api = RetrofitClient.createApi(SearchPageApi::class.java)
                val response = api.getArtist()
                Log.d("hyj", "歌手排行榜接口状态码: ${response.code}, 数据量: ${response?.list?.artists?.size}")
                if (response.code == 200) {
                    _artistFlow.value = response.list.artists
                } else {
                    Log.w("hyj", "歌手排行榜接口请求成功但业务失败，状态码: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("hyj", "获取推荐歌单失败: ${e.message}", e)
                _artistFlow.value = emptyList()
            }
        }
    }

    fun fetchGenre() {
        launchRequest {
            try {
                val api = RetrofitClient.createApi(SearchPageApi::class.java)
                val response = api.getGenre()
                Log.d("hyj", "曲风排行榜接口状态码: ${response.code}, 数据量: ${response?.data?.size}")
                if (response.code == 200) {
                    _genreFlow.value = response.data.get(0).childrenTags?:emptyList()
                } else {
                    Log.w("hyj", "歌手排行榜接口请求成功但业务失败，状态码: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("hyj", "获取推荐歌单失败: ${e.message}", e)
                _genreFlow.value = emptyList()
            }
        }
    }
    fun fetchAlbum() {
        launchRequest {
            try {
                val api = RetrofitClient.createApi(SearchPageApi::class.java)
                val response = api.getAlbunm()
                Log.d("hyj", "曲风排行榜接口状态码: ${response.code}, 数据量: ${response.albums.size}")
                if (response.code == 200) {
                    _albumFlow.value = response.albums
                } else {
                    Log.w("hyj", "歌手排行榜接口请求成功但业务失败，状态码: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("hyj", "获取推荐歌单失败: ${e.message}", e)
                _albumFlow.value = emptyList()
            }
        }
    }

    fun fetchRadio() {
        launchRequest {
            try {
                val api = RetrofitClient.createApi(SearchPageApi::class.java)
                val response = api.getRadio()
                Log.d("hyj", "曲风排行榜接口状态码: ${response.code}, 数据量: ${response.djRadios.size}")
                if (response.code == 200) {
                    _radioFlow.value = response.djRadios
                } else {
                    Log.w("hyj", "歌手排行榜接口请求成功但业务失败，状态码: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("hyj", "获取推荐歌单失败: ${e.message}", e)
                _radioFlow.value = emptyList()
            }
        }
    }
}