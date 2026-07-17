package com.example.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.base.BaseViewModel
import com.example.net.RetrofitClient
import com.example.player.model.SongUrlData
import com.example.player.model.SongUrlResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    /** 是否正在播放 */
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    /** 当前播放进度 (0-100) */
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    /** 当前播放时间 (毫秒) */
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    /** 总时长 (毫秒) */
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    // ========== 歌曲信息 ==========

    /** 当前播放的歌曲 */
    private val _currentSong = MutableLiveData<SongUrlData?>()
    val currentSong: LiveData<SongUrlData?> = _currentSong

    /** 歌曲列表 */
    private val _songList = MutableLiveData<List<SongUrlData>>(emptyList())
    val songList: LiveData<List<SongUrlData>> = _songList

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

    // ========== 播放控制 ==========

    /** 切换播放/暂停 */
    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    /** 设置播放状态 */
    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    /** 更新播放进度 */
    fun updateProgress(position: Long, total: Long) {
        _currentPosition.value = position
        _duration.value = total
        _progress.value = if (total > 0) ((position * 100) / total).toInt() else 0
    }

    /** 拖动进度条跳转 */
    fun seekTo(progress: Int) {
        _progress.value = progress
    }

    // ========== 歌曲列表管理 ==========

    /** 设置歌曲列表 */
    fun setSongList(songs: List<SongUrlData>) {
        _songList.value = songs
        if (songs.isNotEmpty()) {
            setCurrentSong(songs[0], 0)
        }
    }

    /** 设置当前播放的歌曲 */
    fun setCurrentSong(song: SongUrlData, index: Int) {
        _currentSong.value = song
        _currentIndex.value = index
        // 注意：这里需要从外部获取歌曲详情（封面、歌手名等）
        // 因为 SongUrlData 只包含播放链接信息，不含元数据
    }

    /** 更新歌曲元数据（封面、歌手名、歌曲名） */
    fun updateSongMetadata(coverUrl: String?, artistName: String, songName: String) {
        _coverUrl.value = coverUrl
        _artistName.value = artistName
        _songName.value = songName
    }

    /** 下一曲 */
    fun nextSong() {
        val songs = _songList.value ?: return
        val currentIdx = _currentIndex.value ?: 0
        if (songs.isNotEmpty()) {
            val nextIdx = (currentIdx + 1) % songs.size
            setCurrentSong(songs[nextIdx], nextIdx)
        }
    }

    /** 上一曲 */
    fun previousSong() {
        val songs = _songList.value ?: return
        val currentIdx = _currentIndex.value ?: 0
        if (songs.isNotEmpty()) {
            val prevIdx = if (currentIdx > 0) currentIdx - 1 else songs.size - 1
            setCurrentSong(songs[prevIdx], prevIdx)
        }
    }

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
                    // 更新当前歌曲信息
                    _currentSong.value = songData
                    // 通知 Fragment 播放歌曲
                    // 实际播放逻辑应该通过 MediaControllerHelper 处理
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
}
