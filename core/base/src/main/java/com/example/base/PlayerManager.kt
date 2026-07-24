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
    var playlist: StateFlow<List<SongDetail>> = _playlist

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

    //播放模式
    private val _playMode = MutableStateFlow(PlayMode.SEQUENTIAL)
    val playMode: StateFlow<PlayMode> = _playMode

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

        val currentIndex = currentList.indexOfFirst { it.id == current.id }

        //根据当前播放模式计算下一首的索引
        val nextIndex = when (_playMode.value) {
            PlayMode.SHUFFLE -> {
                //如果列表只有1首歌，就还是播放它自己
                if (currentList.size <= 1) {
                    currentIndex
                } else {
                    //随机找一首并且保证和当前这首不一样
                    var randomIndex = currentList.indices.random()
                    while (randomIndex == currentIndex) {
                        randomIndex = currentList.indices.random()
                    }
                    randomIndex
                }
            }
            PlayMode.SEQUENTIAL -> {
                //顺序播放：使用取模运算%，实现列表循环
                if (currentIndex == -1) 0 else (currentIndex + 1) % currentList.size
            }
            PlayMode.SINGLE_LOOP -> {
                seekTo(0)
                currentIndex
            }
        }

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

        //计算上一首的索引如果已经是第一首就跳到最后一首
        val previousIndex = when (_playMode.value) {
            //随机播放
            PlayMode.SHUFFLE -> {
                if (currentList.size <= 1) {
                    currentIndex
                } else {
                    var randomIndex = currentList.indices.random()
                    while (randomIndex == currentIndex) {
                        randomIndex = currentList.indices.random()
                    }
                    randomIndex
                }
            }
            PlayMode.SEQUENTIAL -> {
                //顺序播放：如果是第一首就跳到最后一首
                if (currentIndex <= 0) currentList.size - 1 else currentIndex - 1
            }
            PlayMode.SINGLE_LOOP -> {
                seekTo(0)
                currentIndex
            }
        }

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

            //添加在则会正在播放歌的后面
            val currentPlayingSong = _currentSong.value

            //找到当前播放歌曲在列表中的索引位置
            val currentIndex = if (currentPlayingSong != null) {
                currentList.indexOfFirst { it.id == currentPlayingSong.id }
            } else {
                -1
            }

            //决定插入的位置
            if (currentIndex != -1) {
                //如果找到了当前正在播放的歌，就把新歌插到它后面
                currentList.add(currentIndex + 1, newSong)
            } else {
                //其他情况加最后就默认追加到列表末尾
                currentList.add(newSong)
            }
            _playlist.value = currentList
        }
    }

    fun startPlayEngine(id: String, songUrl: String) {
        mediaControllerHelper?.playSingleSong(id, songUrl)
    }
    fun updatePlaylist(newList: List<SongDetail>) {
        _playlist.value = newList
    }

    fun togglePlayMode() {
        _playMode.value = when(_playMode.value){
            PlayMode.SEQUENTIAL -> PlayMode.SHUFFLE
            PlayMode.SHUFFLE -> PlayMode.SINGLE_LOOP
            PlayMode.SINGLE_LOOP -> PlayMode.SEQUENTIAL
        }
    }
}

enum class PlayMode {
    SEQUENTIAL, //顺序播放
    SHUFFLE ,
    SINGLE_LOOP
}