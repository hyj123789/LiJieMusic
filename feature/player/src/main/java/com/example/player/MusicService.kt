package com.example.player

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Build
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * 音乐播放后台服务
 *
 * 继承 MediaSessionService，作用：
 * 1. 持有 ExoPlayer 实例，App 退到后台时播放不会中断
 * 2. 自动生成通知栏控制器（上一曲、播放/暂停、下一曲）
 * 3. 支持锁屏控制、蓝牙耳机控制
 *
 * 生命周期：
 *   系统启动 → onCreate() → onGetSession()
 *   Fragment 通过 MediaController 连接进来 → 控制播放
 *   用户主动停止或系统回收 → onDestroy()
 */
class MusicService : MediaSessionService() {

    // ========== 成员变量 ==========

    /**
     * MediaSession：把 ExoPlayer 的播放能力暴露给系统
     * 系统拿到它之后，就能在通知栏、锁屏、蓝牙等地方显示播放控制
     */
    private var mediaSession: MediaSession? = null

    /**
     * 是否是前台服务
     * 用于标记当前服务是否已经调用了 startForeground()
     * 避免重复调用导致崩溃
     */
    private var isForegroundService = false

    // ========== 生命周期 ==========

    /**
     * 服务创建时调用（只调一次）
     * 在这里初始化 ExoPlayer 和 MediaSession
     */
    override fun onCreate() {
        super.onCreate()

        // 1. 创建 ExoPlayer 实例
        val player = ExoPlayer.Builder(this)
            // 设置音频属性：告诉系统这是音乐类型，不是铃声或通知音
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)  // 内容类型：音乐
                    .setUsage(C.USAGE_MEDIA)                     // 用途：媒体播放
                    .build(),
                /* handleAudioFocus = true */
                // 自动处理音频焦点：
                //   - 来电话时自动暂停
                //   - 导航语音时自动降低音量
                //   - 其他 App 播放音频时自动暂停
                true
            )
            // 跨 App 音频焦点处理（比如和导航 App 共存）
            .setHandleAudioBecomingNoisy(true)
            // 当耳机拔出时自动暂停，避免外放尴尬
            .build()

        // 2. 创建 MediaSession，把 ExoPlayer 包装进去
        mediaSession = MediaSession.Builder(this, player)
            // 设置 MediaSession 的 PendingIntent，点击通知栏时打开 App
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    // TODO: 这里改成你 App 主界面的 Activity，点击通知栏时跳回去
                    // Intent(this, MainActivity::class.java),
                    Intent().apply { setPackage(packageName) },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()

        // 3. 监听播放状态，管理前台服务
        //    播放时让服务进入前台（显示通知栏），暂停时可以退出前台
        player.addListener(object : Player.Listener {

            /**
             * isPlaying 变化时回调（play() / pause() 都会触发）
             * isPlaying = true  → 正在播放
             * isPlaying = false → 暂停或停止
             */
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    // 正在播放 → 进入前台服务模式
                    // 这样系统不会因为内存不足杀掉这个服务
                    // 同时会在通知栏显示播放控制器
                    startForegroundIfNeeded()
                } else {
                    // 暂停了 → 可以退出前台模式
                    // 但不停止服务，用户可能还会继续播放
                    stopForegroundIfNeeded()
                }
            }
        })
    }

    /**
     * 系统要求必须实现的方法
     *
     * 当 Fragment 通过 MediaController 连接时，系统会调用这个方法
     * 返回我们创建好的 MediaSession，这样 Fragment 就能控制播放了
     *
     * @param controllerInfo 连接方的信息（哪个 App、哪个组件在连接）
     * @return MediaSession 实例，返回 null 表示拒绝连接
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    /**
     * 服务销毁时调用
     * 必须在这里释放所有资源，否则会内存泄漏
     */
    override fun onDestroy() {
        // 先退出前台模式
        stopForegroundIfNeeded()

        // 释放 MediaSession 和 ExoPlayer
        mediaSession?.run {
            player.release()   // 释放播放器（解码器、音频轨道等）
            release()          // 释放 MediaSession 自身
        }
        mediaSession = null

        super.onDestroy()
    }

    // ========== 前台服务管理 ==========

    /**
     * 进入前台服务模式
     *
     * 调用 startForeground() 后：
     * 1. 服务优先级提高，系统不会轻易杀掉
     * 2. 通知栏会显示一个持久通知（播放控制器）
     *
     * 注意：Android 12+ 必须在 5 秒内调用 startForeground()
     * 否则系统会抛出 ANR 异常
     */
    private fun startForegroundIfNeeded() {
        if (isForegroundService) return  // 已经是前台了，不重复调用

        // 获取 MediaSession 通知信息
        // Media3 会自动根据当前播放的歌曲信息（标题、歌手、封面）生成通知
        val notification = mediaSession?.player?.let { player ->
            // Media3 的 MediaNotificationManager 会自动管理通知
            // 这里我们用一个简单的占位通知，实际的通知由 Media3 自动更新
            createNotification()
        }

        if (notification != null) {
            // startForeground(通知ID, 通知对象)
            // 通知ID：用来标识这个通知，同一 ID 会覆盖之前的
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ 需要指定前台服务类型
                startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
            isForegroundService = true
        }
    }

    /**
     * 退出前台服务模式
     *
     * stopForeground(true)：
     *   - true  = 同时移除通知栏的通知
     *   - false = 保留通知但不再前台
     *
     * 退出前台后服务还在运行，只是优先级降低了
     * 系统内存不足时可能会杀掉
     */
    private fun stopForegroundIfNeeded() {
        if (!isForegroundService) return

        @Suppress("DEPRECATION")
        stopForeground(true)
        isForegroundService = false
    }

    /**
     * 创建通知栏通知
     *
     * 这里创建一个基础通知，Media3 会自动用 MediaSession 的元数据
     * （标题、歌手、封面）来更新通知内容
     *
     * 实际项目中你可能需要自定义通知样式，这里用最简单的实现
     */
    private fun createNotification(): android.app.Notification {
        // 创建通知渠道（Android 8.0+ 必须）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                "音乐播放",
                android.app.NotificationManager.IMPORTANCE_LOW  // LOW = 不发声，只显示
            ).apply {
                description = "音乐播放控制"
            }
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("正在播放")
            .setContentText("准备播放...")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)  // 不可滑动删除
            .build()
    }

    // ========== 伴生对象（常量） ==========

    companion object {
        /** 通知 ID，用于标识这个前台服务的通知 */
        private const val NOTIFICATION_ID = 1001

        /** 通知渠道 ID，Android 8.0+ 需要 */
        private const val CHANNEL_ID = "music_playback_channel"
    }
}
