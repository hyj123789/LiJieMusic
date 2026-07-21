package com.example.base

import android.annotation.SuppressLint
import android.content.Context
import android.media.browse.MediaBrowser
import android.util.Log
import androidx.media3.common.MediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object PlayerManager : MediaControllerHelper.MediaControllerListener{

    //声明一个helper
    @SuppressLint("StaticFieldLeak")
    private var mediaControllerHelper: MediaControllerHelper? = null

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    //歌单
    private val _playlist = MutableStateFlow<List<SongDetail>>(emptyList())
    val playlist: StateFlow<List<SongDetail>> = _playlist

    //当前歌曲
    private val _currentSong = MutableStateFlow<SongDetail?>(null)
    val currentSong: StateFlow<SongDetail?> = _currentSong

    //是否播放
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    //当前位置
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    //时间
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    //初始化播放器
    fun initPlayer(context: Context) {
        if (mediaControllerHelper == null) {
            //用applicationContext防止内存泄漏
            mediaControllerHelper = MediaControllerHelper(context.applicationContext, this)
            mediaControllerHelper?.connect()
        }
    }

    fun playSong(id: String, songName: String, artistName: String) {
        //拼装基础实体类
        val newSong = SongDetail(
            id = id.toLongOrNull() ?: 0L,
            name = songName,
            ar = listOf(Artist(id = 0L, name = artistName)),
            al = Album(id = 0L, name = "", picUrl = ""),
            dt = 0,
            fee = 0
        )

        //维护播放列表
        val currentList = _playlist.value.toMutableList()
        if (currentList.none { it.id == newSong.id }) {
            currentList.add(newSong)
            _playlist.value = currentList
        }

        //改变当前播放状态，触发网络请求逻辑
        _currentSong.value = newSong
    }


    //暂停
    fun togglePlayPause() {
        mediaControllerHelper?.togglePlayPause()
    }
    //下一首
    fun next() {
        val currentList = _playlist.value
        val current = _currentSong.value

        //如果列表是空的直接返回
        if (currentList.isEmpty()) return

        //如果当前没有播放歌曲，默认播放列表第一首
        if (current == null) {
            _currentSong.value = currentList[0]
            return
        }

        //查找当前歌曲在列表里的索引
        val currentIndex = currentList.indexOfFirst { it.id == current.id }

        //计算下一首的索引（使用取模运算 %，实现列表循环播放）
        val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % currentList.size

        Log.d("hyj", "准备切换下一首，当前索引: $currentIndex，下一首索引: $nextIndex")
        _currentSong.value = currentList[nextIndex]
    }
    //前一首
    fun previous() {
        val currentList = _playlist.value
        val current = _currentSong.value

        if (currentList.isEmpty()) return
        if (current == null) {
            _currentSong.value = currentList.last()
            return
        }

        val currentIndex = currentList.indexOfFirst { it.id == current.id }

        //计算上一首的索引如果已经是第一首，就跳到最后一首
        val previousIndex = if (currentIndex <= 0) currentList.size - 1 else currentIndex - 1

        Log.d("hyj", "准备切换上一首，下一首索引: $previousIndex")

        //改变歌曲状态
        _currentSong.value = currentList[previousIndex]
    }
    //跳转某首
    fun seekTo(positionMs: Long) {
        mediaControllerHelper?.seekTo(positionMs)
    }
    //清空歌单
    fun clearPlaylist() {
        _playlist.value = emptyList()
        _currentSong.value = null
        mediaControllerHelper?.pause()
    }

    override fun onConnected() {

    }

    override fun onPlayingStateChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
        if (isPlaying) {
            startProgressTicker()
        } else {
            stopProgressTicker()
        }
    }

    override fun onDurationChanged(duration: Long) {
        _duration.value = duration
    }

    override fun onPositionChanged(position: Long) {
        _currentPosition.value = position
    }

    override fun onMediaItemChanged(mediaItem: MediaItem) {
        //TODO("Not yet implemented")
    }



    override fun onPlaybackEnded() {
        PlayerManager.next()
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                mediaControllerHelper?.let {
                    _currentPosition.value = it.getCurrentPosition()
                    _duration.value = it.getDuration()
                }
                delay(200)
            }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
    }

   //移除某首歌
    fun removeSong(song: SongDetail) {
        //过滤掉被删掉的那首歌更新播放列表
        val newList = _playlist.value.filter { it.id != song.id }
        _playlist.value = newList

        //如果用户删掉的刚好是正在播放的这首歌就停止发声
        if (_currentSong.value?.id == song.id) {
            _currentSong.value = null
            //暂停
            mediaControllerHelper?.pause()
            _isPlaying.value = false
        }
    }

    //添加歌到下一首
    fun addSongToPlaylist(id: String, songName: String, artistName: String) {
        val newSong = SongDetail(
            id = id.toLongOrNull() ?: 0L,
            name = songName,
            ar = listOf(Artist(id = 0L, name = artistName)),
            al = Album(id = 0L, name = "", picUrl = ""),
            dt = 0,
            fee = 0
        )

        val currentList = _playlist.value.toMutableList()
        if (currentList.none { it.id == newSong.id }) {
            currentList.add(newSong)
            _playlist.value = currentList
        }
    }

    fun startPlayEngine(id: String, songUrl: String) {
        mediaControllerHelper?.playSingleSong(id, songUrl)
    }

}