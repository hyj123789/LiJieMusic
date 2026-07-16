package com.example.player

import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Player 模块播放测试
 *
 * 在真机/模拟器上运行，验证 MusicService + MediaControllerHelper 的播放流程。
 *
 * 运行方式：
 *   ./gradlew :feature:player:connectedAndroidTest
 *
 * 或者在 Android Studio 中点击类名左侧的绿色三角运行。
 */
@RunWith(AndroidJUnit4::class)
class PlayerPlaybackTest {

    companion object {
        // TODO: 替换成你要测试的临时 URL
        private const val TEST_MUSIC_URL = "http://m801.music.126.net/20260716194334/ad9df6256447e512bf7956b32c69f1e5/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/36333442293/4f0c/cef1/9715/0434d69957ac314e1b6e0c30f8c1a305.mp3?vuutv=p8/yWWCAO4MQH+Ww3kS2ru7CBY2+6WwkIfD7k8MMlyASmMHNV+YiH2SmcVGD+fowI9Hmre5X5TYvzxh8Ms8s1d3f1nqdSp0en+KnmPqLwv8="
        private const val TEST_SONG_ID = "test_song_001"
        private const val CONNECT_TIMEOUT = 10L
        private const val PLAY_TIMEOUT = 15L
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var mediaControllerHelper: MediaControllerHelper? = null

    // Latches 用于同步异步回调
    private var connectLatch = CountDownLatch(1)
    private var playingLatch = CountDownLatch(1)
    private var pausedLatch = CountDownLatch(1)
    private var readyLatch = CountDownLatch(1)
    private var endedLatch = CountDownLatch(1)

    private var lastIsPlaying = false
    private var lastDuration = 0L

    private val listener = object : MediaControllerHelper.MediaControllerListener {
        override fun onConnected() {
            connectLatch.countDown()
        }

        override fun onPlayingStateChanged(isPlaying: Boolean) {
            lastIsPlaying = isPlaying
            if (isPlaying) {
                playingLatch.countDown()
            } else {
                pausedLatch.countDown()
            }
        }

        override fun onDurationChanged(duration: Long) {
            lastDuration = duration
            readyLatch.countDown()
        }

        override fun onPositionChanged(position: Long) {}
        override fun onMediaItemChanged(mediaItem: MediaItem) {}
        override fun onPlaybackEnded() {
            endedLatch.countDown()
        }
    }

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // MediaController 的所有操作都必须在主线程
        runOnMainThread {
            mediaControllerHelper = MediaControllerHelper(context, listener)
            mediaControllerHelper?.connect()
        }

        assertTrue(
            "MediaController 应该在 ${CONNECT_TIMEOUT}s 内连接成功",
            connectLatch.await(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        )
    }

    @After
    fun tearDown() {
        runOnMainThread {
            mediaControllerHelper?.disconnect()
            mediaControllerHelper = null
        }
    }

    /**
     * 测试 1：MediaController 能否成功连接到 MusicService
     */
    @Test
    fun testConnectionToMusicService() {
        assertNotNull("MediaControllerHelper 应该已初始化", mediaControllerHelper)
    }

    /**
     * 测试 2：播放单首歌曲，验证播放状态变为 isPlaying = true
     */
    @Test
    fun testPlaySingleSong() {
        resetLatches()
        runOnMainThread {
            mediaControllerHelper?.playSingleSong(TEST_SONG_ID, TEST_MUSIC_URL)
        }

        val started = playingLatch.await(PLAY_TIMEOUT, TimeUnit.SECONDS)
        assertTrue("歌曲应该开始播放（检查 URL 是否有效）", started)
        assertTrue("isPlaying 应该为 true", lastIsPlaying)
    }

    /**
     * 测试 3：播放后暂停，验证 isPlaying 变为 false
     */
    @Test
    fun testPauseAfterPlay() {
        resetLatches()
        runOnMainThread {
            mediaControllerHelper?.playSingleSong(TEST_SONG_ID, TEST_MUSIC_URL)
        }

        assertTrue("歌曲应该开始播放", playingLatch.await(PLAY_TIMEOUT, TimeUnit.SECONDS))

        pausedLatch = CountDownLatch(1)
        runOnMainThread {
            mediaControllerHelper?.pause()
        }

        val paused = pausedLatch.await(5, TimeUnit.SECONDS)
        assertTrue("暂停后 isPlaying 应该为 false", paused)
        assertFalse("isPlaying 应该为 false", lastIsPlaying)
    }

    /**
     * 测试 4：播放后能获取到有效的 duration
     */
    @Test
    fun testDurationIsAvailable() {
        resetLatches()
        runOnMainThread {
            mediaControllerHelper?.playSingleSong(TEST_SONG_ID, TEST_MUSIC_URL)
        }

        val ready = readyLatch.await(PLAY_TIMEOUT, TimeUnit.SECONDS)
        assertTrue("播放器应该回调 onDurationChanged", ready)

        var duration = 0L
        runOnMainThread {
            duration = mediaControllerHelper?.getDuration() ?: 0
        }
        assertTrue("duration 应该大于 0（当前: ${duration}ms）", duration > 0)
    }

    /**
     * 测试 5：播放后能获取到当前位置
     */
    @Test
    fun testCurrentPositionIsUpdated() {
        resetLatches()
        runOnMainThread {
            mediaControllerHelper?.playSingleSong(TEST_SONG_ID, TEST_MUSIC_URL)
        }

        assertTrue("歌曲应该开始播放", playingLatch.await(PLAY_TIMEOUT, TimeUnit.SECONDS))

        // 等 2 秒让播放推进
        Thread.sleep(2000)

        var position = 0L
        runOnMainThread {
            position = mediaControllerHelper?.getCurrentPosition() ?: 0
        }
        assertTrue("播放位置应该大于 0（当前: ${position}ms）", position > 0)
    }

    /**
     * 测试 6：完整播放歌曲，等待播放结束
     * 这个测试会一直等到歌曲播完，用于验证完整播放流程
     */
    @Test
    fun testFullPlayback() {
        resetLatches()
        runOnMainThread {
            mediaControllerHelper?.playSingleSong(TEST_SONG_ID, TEST_MUSIC_URL)
        }

        // 等待播放开始
        assertTrue("歌曲应该开始播放", playingLatch.await(PLAY_TIMEOUT, TimeUnit.SECONDS))

        // 获取歌曲总时长
        var duration = 0L
        runOnMainThread {
            duration = mediaControllerHelper?.getDuration() ?: 0
        }
        println("歌曲总时长: ${duration}ms (${duration / 1000}s)")

        // 等待歌曲播完（duration + 5 秒缓冲）
        val playCompleteTimeout = (duration / 1000) + 10
        println("等待播放完成，超时时间: ${playCompleteTimeout}s")

        val ended = endedLatch.await(playCompleteTimeout, TimeUnit.SECONDS)
        assertTrue("歌曲应该播完", ended)
        println("歌曲播放完成!")
    }

    /**
     * 在主线程执行代码块，阻塞等待完成
     */
    private fun runOnMainThread(block: () -> Unit) {
        val latch = CountDownLatch(1)
        mainHandler.post {
            block()
            latch.countDown()
        }
        latch.await(5, TimeUnit.SECONDS)
    }

    private fun resetLatches() {
        playingLatch = CountDownLatch(1)
        pausedLatch = CountDownLatch(1)
        readyLatch = CountDownLatch(1)
        endedLatch = CountDownLatch(1)
    }
}
