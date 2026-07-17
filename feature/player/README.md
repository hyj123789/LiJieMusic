# Player Feature 模块

## 概述

Player 模块是音乐播放器的核心功能模块，负责音乐播放、控制和 UI 展示。

## 架构

采用 MVVM 架构模式：

```
PlayerFragment (View)
    ↓
PlayerViewModel (ViewModel)
    ↓
MediaControllerHelper (Media3 Controller)
    ↓
MusicService (Media3 Service)
```

## 核心组件

### 1. PlayerFragment

播放器界面，负责：
- 展示播放器 UI（歌曲信息、进度条、控制按钮）
- 处理用户交互（播放/暂停、上下曲、进度拖动、音质切换）
- 观察 ViewModel 状态并更新 UI
- 连接 MediaController 控制 MusicService

### 2. PlayerViewModel

播放器业务逻辑，负责：
- 管理播放状态（播放/暂停/进度）
- 管理歌曲列表
- 处理播放控制逻辑
- 发起网络请求（检查歌曲可用性、获取播放链接）

### 3. MusicService

后台音乐播放服务，负责：
- 持有 ExoPlayer 实例，App 退到后台时播放不会中断
- 自动生成通知栏控制器（上一曲、播放/暂停、下一曲）
- 支持锁屏控制、蓝牙耳机控制

### 4. MediaControllerHelper

MediaController 帮助类，负责：
- 管理与 MusicService 的连接
- 提供播放控制方法（播放、暂停、上下曲、进度跳转）
- 监听播放状态并回调给 ViewModel

### 5. PlayerApi

播放器 API 接口，包括：
- `checkMusicIsAvailable`: 检查歌曲是否可用
- `getMusicUrl`: 获取歌曲播放链接
- `getSongDetail`: 获取歌曲详情

## 数据模型

### MusicAvailable
歌曲可用性检查响应

### Song
歌曲播放链接响应

### Data
歌曲播放链接数据，包含：
- `id`: 歌曲 ID
- `url`: 播放链接
- `br`: 比特率
- `size`: 文件大小
- `time`: 时长（毫秒）
- `level`: 音质等级

### SongDetail
歌曲详情，包含：
- `id`: 歌曲 ID
- `name`: 歌曲名
- `ar`: 歌手列表
- `al`: 专辑信息（含封面 URL）
- `dt`: 时长（毫秒）

## 使用方法

### 1. 初始化播放器

```kotlin
class YourFragment : BaseFragment<YourBinding>(YourBinding::inflate) {

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun initView() {
        super.initView()
        // 播放器会自动连接 MusicService
    }
}
```

### 2. 播放歌曲

```kotlin
// 播放单首歌曲
playerViewModel.fetchMusicUrl(songId, "standard")

// 播放歌曲列表
playerViewModel.setSongList(songList)
```

### 3. 控制播放

```kotlin
// 播放/暂停
playerViewModel.togglePlayPause()

// 上一曲/下一曲
playerViewModel.previousSong()
playerViewModel.nextSong()

// 跳转进度
playerViewModel.seekTo(progress)
```

### 4. 观察状态

```kotlin
// 观察播放状态
playerViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
    // 更新 UI
}

// 观察进度
playerViewModel.progress.observe(viewLifecycleOwner) { progress ->
    // 更新进度条
}

// 观察歌曲信息
playerViewModel.songName.observe(viewLifecycleOwner) { songName ->
    // 更新歌曲名
}
```

## 音质等级

| 等级 | 代码 | 说明 |
|------|------|------|
| 标准 | standard | 标准音质 |
| 高品质 | higher | 高品质 |
| 无损 | exhigh | 无损音质 |
| Hi-Res | hires | Hi-Res 音质 |
| 高清臻音 | jyeffect | 高清臻音 |
| 超清母带 | jymaster | 超清母带 |

## 注意事项

1. **权限要求**：
   - `INTERNET`: 网络权限
   - `POST_NOTIFICATIONS`: 通知权限（Android 13+）
   - `FOREGROUND_SERVICE`: 前台服务权限
   - `FOREGROUND_SERVICE_MEDIA_PLAYBACK`: 媒体播放前台服务权限

2. **Android 版本兼容**：
   - minSdk = 24 (Android 7.0)
   - Android 13+ 需要动态申请通知权限
   - Android 12+ 前台服务启动限制

3. **生命周期管理**：
   - MediaController 在 Fragment onDestroyView 时断开
   - 进度更新在 onPause 时暂停，onResume 时恢复
   - MusicService 在 App 退到后台时继续播放

4. **音频焦点**：
   - 自动处理音频焦点（来电话时暂停、导航时降低音量）
   - 耳机拔出时自动暂停

## TODO

- [ ] 实现歌曲详情查询（封面、歌手名）
- [ ] 实现收藏功能
- [ ] 实现评论功能
- [ ] 实现分享功能
- [ ] 实现播放列表功能
- [ ] 实现歌词显示
- [ ] 实现后台播放通知栏控制
- [ ] 实现锁屏控制
- [ ] 实现蓝牙耳机控制
- [ ] 实现播放模式切换（单曲循环、列表循环、随机播放）
- [ ] 实现音量控制
- [ ] 实现播放历史记录