package com.example.player

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import com.example.base.BaseFragment
import com.example.player.databinding.FragmentPlayerBinding
import com.example.util.ToastUtil
import kotlinx.coroutines.launch

/**
 * 播放器 Fragment
 *
 * 职责：
 * 1. 展示播放器 UI
 * 2. 处理用户交互（播放/暂停、上下曲、进度拖动）
 * 3. 观察 ViewModel 状态并更新 UI
 * 4. 连接 MediaController 控制 MusicService
 */
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(FragmentPlayerBinding::inflate),
    MediaControllerHelper.MediaControllerListener {

    private val viewModel: PlayerViewModel by viewModels()
    private var mediaControllerHelper: MediaControllerHelper? = null

    private val qualityOptions = arrayOf("标准", "高品质", "无损")
    private var currentQualityIndex = 0

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
            // TODO: 跳转到评论页面
            ToastUtil.popToast("评论功能开发中", requireContext())
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
