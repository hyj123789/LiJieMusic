package com.example.player

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.base.BaseViewModel
import com.example.net.RetrofitClient
import com.example.player.model.Lyric
import com.example.player.model.LyricParser
import com.example.player.model.SongUrlData
import com.example.player.model.SongUrlResponse
import com.example.util.ToastUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 播放器 ViewModel
 *
 * 职责：
 * 1. 管理播放状态（播放/暂停/进度）
 * 2. 管理歌曲列表
 * 3. 处理播放控制逻辑
 */
class PlayerViewModel : BaseViewModel() {

    // ========== 播放状态 ==========

    /** 当前播放进度 (0-100) */
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    //判断该歌曲是否喜欢
    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked

    // ========== 歌曲信息 ==========

    /** 当前播放的歌曲 */
    private val _currentSong = MutableLiveData<SongUrlData?>()
    val currentSong: LiveData<SongUrlData?> = _currentSong

    /** 当前歌曲索引 */
    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    /** 歌曲封面 URL */
    private val _coverUrl = MutableLiveData<String?>()
    val coverUrl: LiveData<String?> = _coverUrl


    /** 歌手名 */
    private val _artistName = MutableLiveData("未知歌手")
    val artistName: LiveData<String> = _artistName

    /** 歌曲名 */
    private val _songName = MutableLiveData("未知歌曲")
    val songName: LiveData<String> = _songName


    // 解析后的歌词列表（含逐字/逐句）
    private val _lyricList = MutableLiveData<List<Lyric>>()
    val lyricList: LiveData<List<Lyric>> = _lyricList

    /** 歌词加载状态 */
    private val _lyricLoading = MutableLiveData(false)
    val lyricLoading: LiveData<Boolean> = _lyricLoading



    // ========== 网络请求 ==========

    /** 检查歌曲是否可用 */
    fun checkMusicAvailable(id: String) {
        launchRequest {
            val api = RetrofitClient.createApi(PlayerApi::class.java)
            val result = api.checkMusicIsAvailable(id)
            if (!result.success) {
                throw Exception(result.message)
            }
        }
    }

    /** 获取歌曲播放链接 */
    fun fetchMusicUrl(id: String, level: String = "standard") {
        launchRequest {
            val api = RetrofitClient.createApi(PlayerApi::class.java)
            val result = api.getMusicUrl(id, level)
            // 处理返回的歌曲链接
            if (result.code == 200 && result.data.isNotEmpty()) {
                val songData = result.data[0]
                if (songData.url != null) {
                    //更新当前歌曲信息
                    _currentSong.value = songData
                }
            }
        }
    }

    /** 获取歌曲详情（封面、歌手名等） */
    fun fetchSongDetail(songId: String) {
        launchRequest {
            val api = RetrofitClient.createApi(PlayerApi::class.java)
            val result = api.getSongDetail(songId)
            if (result.code == 200 && result.songs.isNotEmpty()) {
                val song = result.songs[0]
                _songName.value = song.name
                _artistName.value = song.ar.joinToString(", ") { it.name }
                _coverUrl.value = song.al.picUrl
            }
        }
    }

    /** 格式化时间 (毫秒 -> mm:ss) */
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    fun fetchLyric(songId: String) {
        viewModelScope.launch {
            _lyricLoading.value = true
            // 切歌时先清空旧歌词，避免 UI 闪烁残留
            _lyricList.value = emptyList()
            try {
                val api = RetrofitClient.createApi(PlayerApi::class.java)
                val response = api.getlyric(songId)
                Log.d("hyj","返回的歌词："+response.toString())
                val lrc = response.lineLyric?.lyric ?: ""
                val yrc = response.wordLyric?.lyric ?: ""
                val parsed = LyricParser.parseLyric(lrc, yrc)
                _lyricList.value = parsed
            } catch (e: Exception) {
                e.printStackTrace()
                _lyricList.value = emptyList()
            } finally {
                _lyricLoading.value = false
            }
        }
    }

    fun checkSongIsLiked(songId: String) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.createApi(PlayerApi::class.java)
                val response = api.checkSongLike("[$songId]")
                Log.d("hyj","是否喜欢的的返回码：${response.code},返回的喜欢的歌曲id${response.ids}")
                if (response.code == 200) {
                    val likedIds = response.ids ?: emptyList()
                    _isLiked.value = likedIds.contains(songId.toLong())
                    Log.d("hyj", "【红心检测】当前播放歌曲ID: $songId")
                    Log.d("hyj", "【红心检测】服务器返回的喜欢列表: $likedIds")
                    Log.d("hyj", "【红心检测】最终匹配结果: ${isLiked.value}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleLike(songId: String, uid: String) {

        val currentStatus = _isLiked.value
        val targetStatus = !currentStatus
        _isLiked.value = targetStatus

        viewModelScope.launch {
            try {
                val api = RetrofitClient.createApi(PlayerApi::class.java)
                val response = api.toggleLikeSong(songId, uid, targetStatus)

                Log.d("hyj","喜欢返回码：${response.code}")
                if (response.code != 200) {
                    _isLiked.value = currentStatus
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("hyj", "点赞接口发生异常了！原因: ${e.message}", e)
                _isLiked.value = currentStatus
            }
        }
    }
}
