package com.example.player

import android.app.AlertDialog
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.base.BaseFragment
import com.example.lijiemusic.core.navigation.RoutePath
import com.example.player.databinding.FragmentPlayerBinding
import com.example.player.model.LyricUtil
import com.example.util.ToastUtil
import kotlinx.coroutines.launch
import com.therouter.router.Route
import kotlinx.coroutines.delay

/**
 * 播放器 Fragment
 *
 * 职责：
 * 1. 展示播放器 UI
 * 2. 处理用户交互（播放/暂停、上下曲、进度拖动）
 * 3. 观察 ViewModel 状态并更新 UI
 * 4. 连接 MediaController 控制 MusicService
 */

@Route(path = RoutePath.PLAYER_MAIN)
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(FragmentPlayerBinding::inflate),
    MediaControllerHelper.MediaControllerListener {
        //定时器
    private var progressJob: kotlinx.coroutines.Job? = null

    //让外面的小播放器也可以使用大播放器的viewmodel
    private val viewModel: PlayerViewModel by activityViewModels()
    private var mediaControllerHelper: MediaControllerHelper? = null

    private val qualityOptions = arrayOf("标准", "高品质", "无损")
    private var currentQualityIndex = 0

    //要传输的数据歌曲的id
    var id = "2692390754"
    var songname  = ""
    var coverurl = ""


    private val lyricAdapter = LyricAdapter()

    /** 进度更新定时器 */
    private val handler = Handler(Looper.getMainLooper())
    private val progressUpdater = object : Runnable {
        override fun run() {
            // 从 MediaController 获取实际播放进度
            mediaControllerHelper?.let {
                val position = it.getCurrentPosition()
                val duration = it.getDuration()
                viewModel.updateProgress(position, duration)
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun initView() {
        super.initView()
        // 初始化 UI 状态
        binding.tvQuality.text = qualityOptions[currentQualityIndex]

        // 初始化 MediaController 连接
        mediaControllerHelper = MediaControllerHelper(requireContext(), this)
        mediaControllerHelper?.connect()


        // 初始化歌词的 RecyclerView
        binding.rvLyrics.adapter = lyricAdapter
        binding.rvLyrics.layoutManager = LinearLayoutManager(requireContext())

        //点击中间区域，切换封面和歌词的显示
        binding.flCenterContent.setOnClickListener {
            if (binding.rvLyrics.visibility == View.VISIBLE) {
                binding.rvLyrics.visibility = View.GONE
                binding.ivAlbumCover.visibility = View.VISIBLE
            } else {
                binding.rvLyrics.visibility = View.VISIBLE
                binding.ivAlbumCover.visibility = View.GONE
                scrollToCurrentLyric()
            }
        }



        // 核心测试代码：主动让 ViewModel 去请求这首测试歌曲的 URL 和 详情
        viewModel.fetchMusicUrl(id)
        viewModel.fetchSongDetail(id)
        viewModel.fetchLyric(id)


    }

    override fun initEvent() {
        super.initEvent()

        // 音质切换点击事件
        binding.tvQuality.setOnClickListener {
            showQualityDialog()
        }

        // 播放/暂停按钮
        binding.btnPlay.setOnClickListener {
            mediaControllerHelper?.togglePlayPause()
        }

        // 上一曲
        binding.btnPrevious.setOnClickListener {
            mediaControllerHelper?.previous()
            viewModel.previousSong()
        }

        // 下一曲
        binding.btnNext.setOnClickListener {
            mediaControllerHelper?.next()
            viewModel.nextSong()
        }

        // 进度条拖动
        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.seekTo(progress)
                    // 计算实际跳转位置
                    val seekPosition = (viewModel.duration.value * progress) / 100
                    mediaControllerHelper?.seekTo(seekPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
                // 用户开始拖动，暂停进度更新
                handler.removeCallbacks(progressUpdater)
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                // 用户停止拖动，恢复进度更新
                handler.post(progressUpdater)
            }
        })

        // 收藏按钮
        binding.btnFavorite.setOnClickListener {
            // TODO: 实现收藏功能
            ToastUtil.popToast("收藏功能开发中", requireContext())
        }

        // 评论按钮
        binding.btnComments.setOnClickListener {
//            TheRouter.build(RoutePath.COMMENT_FRAGMENT) //跳往评论
//                .withString("songId", id.toString())
//                .withString("songName", viewModel.songName.value)
//                .withString("coverUrl", viewModel.coverUrl.value)
//                .navigation(requireContext())
//            ToastUtil.popToast("正在跳往评论曲", requireContext())

            //利用DeepLink深层链接跳转
            val songId = id
            val songName = songname
            // 注意：如果是图片网址，里面有斜杠等特殊字符，最好 Encode 一下防止解析错误
            val coverUrl = Uri.encode(coverurl )

            //拼出我们定义的那个网址暗号
            val uriString = "lijiemusic://comment?songId=$songId&songName=$songName&coverUrl=$coverUrl"

            //Navigation会自动跨模块找到它！
            findNavController().navigate(Uri.parse(uriString))
        }

        // 分享按钮
        binding.btnShare.setOnClickListener {
            // TODO: 实现分享功能
            ToastUtil.popToast("分享功能开发中", requireContext())
        }

        // 更多详情按钮
        binding.btnDetail.setOnClickListener {
            // TODO: 显示歌曲详情
            ToastUtil.popToast("详情功能开发中", requireContext())
        }

        // 返回按钮
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // 播放列表按钮
        binding.btnPlaylist.setOnClickListener {
            // TODO: 显示播放列表
            ToastUtil.popToast("播放列表功能开发中", requireContext())
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewModel.currentSong.observe(viewLifecycleOwner) { songData ->
            if (songData != null && !songData.url.isNullOrEmpty()) {
                //调用ljh封装好的方法开始播放
                mediaControllerHelper?.playSingleSong(songData.id.toString(), songData.url)
            }
            if (songData != null) {
                Log.d("hyj", "拿到的歌曲URL是: ${songData.url}")

                if (!songData.url.isNullOrEmpty()) {
                    mediaControllerHelper?.playSingleSong(songData.id.toString(), songData.url)
                } else {
                    Log.d("hyj", "糟糕，URL是空的！没法播！")
                }
            }
        }

        viewModel.coverUrl.observe(viewLifecycleOwner) { url ->
            //当网络请求成功url有值的时候，这里的代码才会被触发！
            if (!url.isNullOrEmpty()) {
                Glide.with(this@PlayerFragment)
                    .load(url)
                    .transform(RoundedCorners(30))
                    .into(binding.ivAlbumCover)

                coverurl = url

            } else {
                Log.d("hyj", "播放器封面链接还是空的！")
            }
        }

        viewModel.lyricData.observe(viewLifecycleOwner) { lrcString ->
            if (!lrcString.isNullOrEmpty()) {
                //不是空音乐
                val parsedList = LyricUtil.parseLyric(lrcString)
                lyricAdapter.submitList(parsedList)
                startProgressTicker()
            } else {
                //如果是空音乐
                lyricAdapter.submitList(emptyList())
                stopProgressTicker()
            }
        }

        // 观察播放状态
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isPlaying.collect { isPlaying ->
                    updatePlayButtonIcon()
                }
            }
        }

        // 观察进度变化
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.progress.collect { progress ->
                    binding.seekBar.progress = progress
                }
            }
        }

        // 观察当前播放时间
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentPosition.collect { position ->
                    binding.tvCurrentTime.text = viewModel.formatTime(position)
                }
            }
        }

        // 观察总时长
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.duration.collect { duration ->
                    binding.tvTotalTime.text = viewModel.formatTime(duration)
                }
            }
        }

        // 观察歌手名
        viewModel.artistName.observe(viewLifecycleOwner) { artist ->
            binding.tvArtist.text = artist

        }

        // 观察歌曲名
        viewModel.songName.observe(viewLifecycleOwner) { songName ->
            binding.tvSong.text = songName
            songname = songName
        }

        // 观察错误信息
        handleApiError(viewModel)
    }

    /**
     * 更新播放按钮图标
     */
    private fun updatePlayButtonIcon() {
        val isPlaying = viewModel.isPlaying.value
        binding.btnPlay.setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
    }



    //时间引擎：利用生命周期安全的协程定时器
// 时间引擎：利用生命周期安全的协程定时器
    private fun startProgressTicker() {
        progressJob?.cancel() //先停掉旧的计时器
        progressJob = viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                //每隔 200 毫秒执行一次
                delay(200)

                //获取当前播放的时间
                val currentPos = mediaControllerHelper?.getCurrentPosition() ?: 0L
                //获得目标行
                val targetLine = lyricAdapter.updateTime(currentPos)

                //只要拿到的不是 -1，直接在这里执行滚动！
                if (binding.rvLyrics.visibility == View.VISIBLE && targetLine != -1) {

                    // 强烈推荐用法：smoothScrollToPosition 自带丝滑的滚动动画，视觉体验满分！
                    binding.rvLyrics.smoothScrollToPosition(targetLine)
                }
            }
        }
    }

    // 让目标行滚动到屏幕最中间的一个小算法
    // 修复后的居中算法：不再做时间判断，直接问 Adapter 当前高亮的是哪行！
    private fun scrollToCurrentLyric() {
        val targetLine = lyricAdapter.getCurrentLineIndex() // 拿到当前行

        if (targetLine >= 0) {
            val layoutManager = binding.rvLyrics.layoutManager as LinearLayoutManager
            // 刚点开的时候，使用带 Offset 的方法瞬间居中，体验最好
            layoutManager.scrollToPositionWithOffset(targetLine, binding.rvLyrics.height / 2)
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
    }


    /**
     * 显示音质选择对话框
     * 需要用到 context，直接放在 UI 层
     */
    private fun showQualityDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("选择音质")
            .setSingleChoiceItems(qualityOptions, currentQualityIndex) { dialog, which ->
                currentQualityIndex = which
                binding.tvQuality.text = qualityOptions[which]
                dialog.dismiss()
                ToastUtil.popToastLong("已切换到${qualityOptions[which]}", requireContext())
                // TODO: 通知 ViewModel 切换音质，重新获取播放链接
                // viewModel.fetchMusicUrl(currentSongId, qualityLevels[which])
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // 开始进度更新
        handler.post(progressUpdater)
    }

    override fun onPause() {
        super.onPause()
        // 停止进度更新
        handler.removeCallbacks(progressUpdater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 断开 MediaController 连接
        mediaControllerHelper?.disconnect()
        mediaControllerHelper = null
        handler.removeCallbacks(progressUpdater)
        stopProgressTicker()
    }

    // ========== MediaControllerListener 实现 ==========

    override fun onConnected() {
        // MediaController 连接成功
        // 可以在这里加载歌曲列表并开始播放
        ToastUtil.popToast("播放器已连接", requireContext())
    }

    override fun onPlayingStateChanged(isPlaying: Boolean) {
        // 播放状态变化
        viewModel.setPlaying(isPlaying)
        updatePlayButtonIcon()
    }

    override fun onDurationChanged(duration: Long) {
        // 总时长变化
        viewModel.updateProgress(viewModel.currentPosition.value, duration)
    }

    override fun onPositionChanged(position: Long) {
        // 播放位置变化
        viewModel.updateProgress(position, viewModel.duration.value)
    }

    override fun onMediaItemChanged(mediaItem: MediaItem) {
        // 歌曲切换
        // 这里可以从 mediaItem 获取歌曲信息并更新 UI
        // 实际项目中可能需要查询歌曲详情 API
    }

    override fun onPlaybackEnded() {
        // 播放结束
        viewModel.setPlaying(false)
        updatePlayButtonIcon()
    }

    companion object {
        /** 音质等级映射 */
        private val qualityLevels = arrayOf("standard", "higher", "exhigh")
    }
}
