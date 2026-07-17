# Jetpack Media3 (ExoPlayer) 入门教程

## 一、简介

Media3 是 Google 官方推出的 Android 媒体播放库，是 ExoPlayer 的继任者。

**它能做什么：**
- 播放本地音频/视频、网络流媒体（HTTP、HLS、DASH）
- 通知栏播放控制（上一曲、播放/暂停、下一曲）
- 锁屏控制、蓝牙耳机控制
- 与 Android Auto、Wear OS 等设备联动

**核心组件：**

| 组件 | 作用 |
|---|---|
| `ExoPlayer` | 播放引擎，负责解码和播放 |
| `MediaSession` | 向系统暴露播放状态，让通知栏/锁屏/蓝牙能控制播放 |
| `MediaController` | 客户端，用来连接 MediaSession 并发送控制指令 |
| `MediaSessionService` | 后台服务，让播放不在前台时也能继续 |

---

## 二、基础用法：最简单的播放

### 2.1 创建 ExoPlayer 实例

```kotlin
import androidx.media3.exoplayer.ExoPlayer

// 在 Activity / Fragment 中创建
val player = ExoPlayer.Builder(context).build()
```

### 2.2 播放一首歌

```kotlin
import androidx.media3.common.MediaItem

// 设置媒体资源
val mediaItem = MediaItem.fromUri("https://example.com/song.mp3")
player.setMediaItem(mediaItem)

// 准备播放（加载资源）
player.prepare()

// 开始播放
player.play()
```

### 2.3 常用控制方法

```kotlin
player.play()           // 播放
player.pause()          // 暂停
player.seekTo(30_000)   // 跳转到 30 秒处
player.seekToNext()     // 下一曲
player.seekToPrevious() // 上一曲
player.stop()           // 停止
player.release()        // 释放资源（必须在不需要时调用！）
```

### 2.4 监听播放状态

```kotlin
player.addListener(object : Player.Listener {

    // 播放状态变化：STATE_IDLE / STATE_BUFFERING / STATE_READY / STATE_ENDED
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> { /* 可以播放了 */ }
            Player.STATE_ENDED -> { /* 播放结束 */ }
            Player.STATE_BUFFERING -> { /* 加载中 */ }
        }
    }

    // isPlaying 变化（播放/暂停切换时触发）
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        // 更新 UI 按钮状态
    }

    // 播放错误
    override fun onPlayerError(error: PlaybackException) {
        // 处理错误
    }
})
```

### 2.5 获取播放信息

```kotlin
player.currentPosition    // 当前播放位置（毫秒）
player.duration           // 总时长（毫秒）
player.isPlaying          // 是否正在播放
player.mediaItemCount     // 播放列表中的歌曲数量
player.currentMediaItem   // 当前正在播放的 MediaItem
```

---

## 三、播放列表

### 3.1 添加多首歌

```kotlin
val songs = listOf(
    MediaItem.fromUri("https://example.com/song1.mp3"),
    MediaItem.fromUri("https://example.com/song2.mp3"),
    MediaItem.fromUri("https://example.com/song3.mp3")
)

player.setMediaItems(songs)  // 设置整个列表
player.prepare()
player.play()
```

### 3.2 动态增删歌曲

```kotlin
// 在末尾添加一首
player.addMediaItem(MediaItem.fromUri("https://example.com/song4.mp3"))

// 在指定位置插入
player.addMediaItem(0, MediaItem.fromUri("https://example.com/new.mp3"))

// 删除指定位置的歌曲
player.removeMediaItem(0)

// 移动歌曲顺序
player.moveMediaItem(2, 0)  // 把第 3 首移到第 1 首
```

### 3.3 设置播放模式

```kotlin
player.repeatMode = Player.REPEAT_MODE_ALL     // 列表循环
player.repeatMode = Player.REPEAT_MODE_ONE    // 单曲循环
player.repeatMode = Player.REPEAT_MODE_OFF    // 不循环

player.shuffleModeEnabled = true  // 随机播放
```

---

## 四、MediaItem 进阶用法

### 4.1 携带自定义数据

每首歌可以附带元数据（标题、歌手、封面等），通过 `MediaMetadata` 传递：

```kotlin
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

val mediaItem = MediaItem.Builder()
    .setUri("https://example.com/song.mp3")
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle("裂缝中的阳光")
            .setArtist("林俊杰")
            .setAlbumTitle("因你而在")
            .build()
    )
    .build()

player.setMediaItem(mediaItem)
```

### 4.2 读取 MediaItem 的元数据

```kotlin
val current = player.currentMediaItem
val title = current?.mediaMetadata?.title   // "裂缝中的阳光"
val artist = current?.mediaMetadata?.artist  // "林俊杰"
```

---

## 五、后台播放（MediaSession + Service）

只靠 ExoPlayer，App 退到后台播放就会停。要实现后台播放 + 通知栏控制，需要 `MediaSessionService`。

### 5.1 创建 MusicService

```kotlin
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
    }

    // 系统要求实现：返回 MediaSession
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }
}
```

### 5.2 注册 Service（AndroidManifest.xml）

在 `feature/player/src/main/AndroidManifest.xml` 中添加：

```xml
<service
    android:name=".MusicService"
    android:exported="true"
    android:foregroundServiceType="mediaPlayback">

    <intent-filter>
        <action android:name="androidx.media3.session.MediaSessionService" />
    </intent-filter>
</service>
```

### 5.3 从 Fragment 连接 Service

```kotlin
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class PlayerFragment : Fragment() {

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private var controller: MediaController? = null

    override fun onStart() {
        super.onStart()

        val sessionToken = SessionToken(
            requireContext(),
            ComponentName(requireContext(), MusicService::class.java)
        )

        controllerFuture = MediaController.Builder(requireContext(), sessionToken)
            .buildAsync()

        controllerFuture.addListener({
            controller = controllerFuture.get()
            // 连接成功，可以控制播放了
            bindPlayerControls()
        }, MoreExecutors.directExecutor())
    }

    override fun onStop() {
        // 释放 controller（不是释放播放器！播放器在 Service 里继续跑）
        MediaController.releaseFuture(controllerFuture)
        controller = null
        super.onStop()
    }

    private fun bindPlayerControls() {
        val ctl = controller ?: return

        // 播放/暂停按钮
        btnPlay.setOnClickListener {
            if (ctl.isPlaying) ctl.pause() else ctl.play()
        }

        // 上一曲 / 下一曲
        btnPrevious.setOnClickListener { ctl.seekToPrevious() }
        btnNext.setOnClickListener { ctl.seekToNext() }

        // 监听状态变化，更新 UI
        ctl.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // 更新播放按钮图标
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                // 更新歌曲名、歌手名
            }
        })
    }
}
```

---

## 六、在当前项目中的推荐架构

```
feature/player/
├── MusicService.kt              ← 后台播放服务，持有 ExoPlayer
├── PlayerFragment.kt            ← UI，通过 MediaController 控制播放
├── PlayerViewModel.kt           ← 管理播放列表数据（从 API 获取）
└── res/layout/fragment_player.xml
```

**数据流：**

```
API 请求歌曲列表
       ↓
PlayerViewModel 解析数据
       ↓
Fragment 用 MediaController 添加歌曲到播放列表
       ↓
MusicService 中的 ExoPlayer 负责播放
       ↓
系统自动生成通知栏控制 + 锁屏控制
```

---

## 七、完整最小示例（可直接跑）

### MusicService.kt

```kotlin
package com.example.player

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MusicService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }
}
```

### PlayerFragment.kt（关键部分）

```kotlin
package com.example.player

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private var controller: MediaController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val token = SessionToken(requireContext(), ComponentName(requireContext(), MusicService::class.java))
        val future = MediaController.Builder(requireContext(), token).buildAsync()
        future.addListener({
            controller = future.get()
            setupControls()
            loadSongs()
        }, MoreExecutors.directExecutor())
    }

    private fun setupControls() {
        val ctl = controller ?: return

        binding.btnPlay.setOnClickListener {
            if (ctl.isPlaying) ctl.pause() else ctl.play()
        }
        binding.btnPrevious.setOnClickListener { ctl.seekToPrevious() }
        binding.btnNext.setOnClickListener { ctl.seekToNext() }

        ctl.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                binding.btnPlay.setImageResource(
                    if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                )
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val metadata = mediaItem?.mediaMetadata
                binding.tvSong.text = metadata?.title ?: ""
                binding.tvArtist.text = metadata?.artist ?: ""
            }
        })
    }

    private fun loadSongs() {
        val songs = listOf(
            buildMediaItem("https://example.com/song1.mp3", "裂缝中的阳光", "林俊杰"),
            buildMediaItem("https://example.com/song2.mp3", "她说", "林俊杰"),
            buildMediaItem("https://example.com/song3.mp3", "可惜没如果", "林俊杰"),
        )
        controller?.apply {
            setMediaItems(songs)
            prepare()
            play()
        }
    }

    private fun buildMediaItem(uri: String, title: String, artist: String): MediaItem {
        return MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .build()
            )
            .build()
    }

    override fun onStop() {
        controller?.release()
        controller = null
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
```

### AndroidManifest.xml（player 模块）

```xml
<service
    android:name=".MusicService"
    android:exported="true"
    android:foregroundServiceType="mediaPlayback">
    <intent-filter>
        <action android:name="androidx.media3.session.MediaSessionService" />
    </intent-filter>
</service>
```

---

## 八、常见问题

### Q: 为什么 `MediaController` 连不上 Service？

检查：
1. AndroidManifest 里是否注册了 Service
2. `exported="true"` 是否设置
3. intent-filter 的 action 是否正确
4. Service 的包名路径是否对

### Q: 通知栏不显示？

需要设置 `foregroundServiceType="mediaPlayback"` 并且 MediaSession 正确创建。

### Q: 播放网络音频没声音？

1. 检查网络权限：`<uses-permission android:name="android.permission.INTERNET" />`
2. URL 是否可访问（浏览器里试试）
3. 查看 Logcat 里的播放错误日志

### Q: ExoPlayer 支持哪些格式？

| 格式 | 支持 |
|---|---|
| MP3 | ✅ |
| AAC | ✅ |
| FLAC | ✅ |
| WAV | ✅ |
| OGG | ✅ |
| OPUS | ✅ |
| MP4 (音频轨道) | ✅ |
| HLS (.m3u8) | ✅ |
| DASH (.mpd) | ✅ |

### Q: 如何显示缓冲/加载状态？

```kotlin
player.addListener(object : Player.Listener {
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> showLoading()
            Player.STATE_READY -> hideLoading()
        }
    }
})
```

---

## 九、常用 API 速查表

| 需求 | API |
|---|---|
| 播放 | `player.play()` |
| 暂停 | `player.pause()` |
| 跳转 | `player.seekTo(positionMs)` |
| 下一曲 | `player.seekToNext()` |
| 上一曲 | `player.seekToPrevious()` |
| 当前位置 | `player.currentPosition` |
| 总时长 | `player.duration` |
| 是否在播放 | `player.isPlaying` |
| 设置循环模式 | `player.repeatMode = REPEAT_MODE_ALL / ONE / OFF` |
| 随机播放 | `player.shuffleModeEnabled = true` |
| 设置播放列表 | `player.setMediaItems(list)` |
| 添加歌曲 | `player.addMediaItem(item)` |
| 删除歌曲 | `player.removeMediaItem(index)` |
| 释放资源 | `player.release()` |
| 当前歌曲信息 | `player.currentMediaItem?.mediaMetadata` |
