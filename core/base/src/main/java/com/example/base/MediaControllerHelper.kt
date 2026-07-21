package com.example.base

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

/**
 * MediaController 帮助类
 *
 * 职责：
 * 1. 管理与 MusicService 的连接
 * 2. 提供播放控制方法（播放、暂停、上下曲、进度跳转）
 * 3. 监听播放状态并回调给 ViewModel
 */
class MediaControllerHelper(
    private val context: Context,
    private val listener: MediaControllerListener
) {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    /**
     * 连接到 MusicService
     */
    fun connect() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, "com.example.player.MusicService")
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            setupPlayerListener()
            listener.onConnected()
        }, MoreExecutors.directExecutor())
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        mediaController?.let {
            it.removeListener(playerListener)
            it.release()
        }
        mediaController = null
        controllerFuture?.cancel(true)
        controllerFuture = null
    }

    /**
     * 设置播放器监听器
     */
    private fun setupPlayerListener() {
        mediaController?.addListener(playerListener)
    }

    /**
     * 播放器状态监听器
     */
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            listener.onPlayingStateChanged(isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    mediaController?.let {
                        listener.onDurationChanged(it.duration)
                    }
                }
                //监听如果是停止播放就调用循环播放，实际就是下一首
                Player.STATE_ENDED -> {
                    listener.onPlaybackEnded()
                }
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.let {
                listener.onMediaItemChanged(it)
            }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            listener.onPositionChanged(newPosition.positionMs)
        }
    }

    // ========== 播放控制方法 ==========

    /**
     * 播放指定歌曲列表
     *
     * @param songIds 歌曲 ID 列表
     * @param startIndex 开始播放的索引
     */
    fun playSongs(songIds: List<String>, startIndex: Int = 0) {
        val mediaItems = songIds.map { id ->
            MediaItem.Builder()
                .setMediaId(id)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .build()
                )
                .build()
        }

        mediaController?.let {
            it.setMediaItems(mediaItems, startIndex, 0)
            it.prepare()
            it.play()
        }
    }

    /**
     * 播放单首歌曲
     *
     * @param songId 歌曲 ID
     * @param url 播放链接
     */
    fun playSingleSong(songId: String, url: String) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(songId)
            .setUri(url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .build()
            )
            .build()

        mediaController?.let {
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }
    }

    /**
     * 继续播放
     */
    fun play() {
        mediaController?.play()
    }

    /**
     * 暂停播放
     */
    fun pause() {
        mediaController?.pause()
    }

    /**
     * 切换播放/暂停
     */
    fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    /**
     * 下一曲
     */
    fun next() {
        mediaController?.seekToNext()
    }

    /**
     * 上一曲
     */
    fun previous() {
        mediaController?.seekToPrevious()
    }

    /**
     * 跳转到指定位置
     *
     * @param positionMs 位置（毫秒）
     */
    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    /**
     * 获取当前播放位置
     */
    fun getCurrentPosition(): Long {
        return mediaController?.currentPosition ?: 0
    }

    /**
     * 获取总时长
     */
    fun getDuration(): Long {
        return mediaController?.duration ?: 0
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return mediaController?.isPlaying ?: false
    }

    /**
     * 设置播放模式（循环、随机等）
     */
    fun setRepeatMode(mode: Int) {
        mediaController?.repeatMode = mode
    }

    /**
     * 设置音量
     */
    fun setVolume(volume: Float) {
        mediaController?.volume = volume
    }

    /**
     * 更新 MediaMetadata（歌曲信息）
     */
    fun updateMetadata(title: String, artist: String, coverUrl: String?) {
        mediaController?.let {
            val metadata = MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setArtworkUri(coverUrl?.let { url -> Uri.parse(url) })
                .build()

            val currentItem = it.currentMediaItem
            if (currentItem != null) {
                val newItem = currentItem.buildUpon()
                    .setMediaMetadata(metadata)
                    .build()
                it.replaceMediaItem(it.currentMediaItemIndex, newItem)
            }
        }
    }

    interface MediaControllerListener {
        fun onConnected()
        fun onPlayingStateChanged(isPlaying: Boolean)
        fun onDurationChanged(duration: Long)
        fun onPositionChanged(position: Long)
        fun onMediaItemChanged(mediaItem: MediaItem)
        fun onPlaybackEnded()
    }
}