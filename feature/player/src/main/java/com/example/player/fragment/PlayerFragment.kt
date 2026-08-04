package com.example.player.fragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.base.BaseFragment
import com.example.base.PlayerManager
import com.example.model.UserManager
import com.example.player.PlayerViewModel
import com.example.player.R
import com.example.player.databinding.FragmentPlayerBinding
import com.example.therouter.RoutePath
import com.example.util.ToastUtil
import com.therouter.router.Route
import kotlinx.coroutines.launch

@Route(path = RoutePath.PLAYER_MAIN)
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(FragmentPlayerBinding::inflate) {
    private val currentUid = UserManager.profile.value?.userId.toString()

    private val viewModel: PlayerViewModel by activityViewModels()

    private val qualityOptions = arrayOf("臻品母带", "臻品全景音", "臻品音质")
    private var currentQualityIndex = 0

    private var id = ""
    private var songName = ""

    private var coverUrl = ""

    private var isLike: Boolean = false

    //是否拖动
    private var isUserSeeking = false

    //动画变量
    private lateinit var rotateAnimator: ObjectAnimator

    override fun initView() {
        super.initView()
        // 初始化 UI 状态
        binding.tvQuality.text = qualityOptions[currentQualityIndex]

        //点击中间区域，切换封面和歌词的显示
        binding.flCenterContent.setOnClickListener {
            if (binding.lvLyrics.isVisible) {
                binding.lvLyrics.visibility = View.GONE
                binding.ivAlbumCover.visibility = View.VISIBLE
                binding.layoutSong.visibility = View.VISIBLE
            } else {
                binding.lvLyrics.visibility = View.VISIBLE
                binding.ivAlbumCover.visibility = View.GONE
                binding.layoutSong.visibility = View.GONE
            }
        }

        // LyricView 拖动浏览后点击确认 → 跳转到选中的歌词时间
        binding.lvLyrics.setOnSeekListener { position ->
            PlayerManager.seekTo(position)
        }

//        if (id.isNotEmpty()) {
//            // 核心测试代码：主动让 ViewModel 去请求这首测试歌曲的 URL 和 详情
//            viewModel.fetchMusicUrl(id)
//            viewModel.fetchSongDetail(id)
//            //获取歌词
//            viewModel.fetchLyric(id)
//            //获取是否喜欢
//            viewModel.checkSongIsLiked(id)
//
//        }else{
//            Log.d("hyj","没有歌曲要播放")
//        }
        initRotateAnimation()
    }

    override fun initEvent() {
        super.initEvent()

        // 音质切换点击事件
        binding.tvQuality.setOnClickListener {
            showQualityDialog()
        }

        // 播放/暂停按钮
        binding.btnPlay.setOnClickListener {
            PlayerManager.togglePlayPause()
        }

        // 上一曲
        binding.btnPrevious.setOnClickListener {
            PlayerManager.previous()
        }

        // 下一曲
        binding.btnNext.setOnClickListener {
            PlayerManager.next()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = PlayerManager.duration.value
                    val seekPosition = (duration * progress) / 100
                    binding.tvCurrentTime.text = viewModel.formatTime(seekPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //正在拖动就不刷新UI
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //手指松开正式指挥播放器跳转到松开的位 置
                seekBar?.let {
                    val duration = PlayerManager.duration.value
                    val seekPosition = (duration * it.progress) / 100
                    PlayerManager.seekTo(seekPosition)
                }
                //恢复manager的自动刷新
                isUserSeeking = false
            }
        })

        //收藏按钮
        binding.btnFavorite.setOnClickListener {
//            if (Islike) {
//                binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
//                ToastUtil.popToast("凭什么不喜欢我了", requireContext())
//            } else {
//                binding.btnFavorite.setImageResource(R.drawable.like1)
//                ToastUtil.popToast("谢谢你的喜欢", requireContext())
//            }
            //设置喜欢状态
            viewModel.toggleLike(id, currentUid)
        }

        // 评论按钮
        binding.btnComments.setOnClickListener {
            //Therouter无法在fragment里面跳转
//            TheRouter.build(RoutePath.COMMENT_FRAGMENT) //跳往评论
//                .withString("songId", id.toString())
//                .withString("songName", viewModel.songName.value)
//                .withString("coverUrl", viewModel.coverUrl.value)
//                .navigation(requireContext())
//            ToastUtil.popToast("正在跳往评论曲", requireContext())

            //利用DeepLink深层链接跳转
            val songId = id
            Log.d("test_lyric", "当前歌曲ID: $id")
            val songName = songName
            // 注意：如果是图片网址，里面有斜杠等特殊字符，最好 Encode 一下防止解析错误
            val coverUrl = Uri.encode(coverUrl)
            //拼出我们定义的那个网址暗号
            val uriString =
                "lijiemusic://comment?songId=$songId&songName=$songName&coverUrl=$coverUrl"
            //Navigation会自动跨模块找到它！
            findNavController().navigate(uriString.toUri())
        }

        // 分享按钮
        binding.btnShare.setOnClickListener {
            val shareSongBottomSheet = ShareSongBottomSheet(id.toLong())
            shareSongBottomSheet.show(childFragmentManager, "ShareSongBottomSheetTag")
            ToastUtil.popToast("前往分享中。。。", requireContext())
        }

        // 更多详情按钮
        binding.btnDetail.setOnClickListener {
            //获取父部的fragment
            val parent = requireParentFragment() as? PlayerContainerFragment
            parent?.goToSecondPage()

            ToastUtil.popToast("前往歌曲百科", requireContext())
        }

        // 返回按钮
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // 播放列表按钮
        binding.btnPlaylist.setOnClickListener {
            //实例化底部菜单
            val playlistDialog = PlaylistBottomSheet()
            playlistDialog.show(childFragmentManager, "PlaylistDialogTag")
            ToastUtil.popToast("播放列表功能加载中", requireContext())
        }
    }

    override fun initObservers() {
        super.initObservers()
        //TODO
        viewLifecycleOwner.lifecycleScope.launch {

        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    // 保存当前歌曲ID
                    viewModel.currentSong.collect { songData ->
                        if (songData != null && !songData.url.isNullOrEmpty()) {
                            id = songData.id.toString()
                            Log.d("Ben", "当前歌曲ID: $id")
                            rotateAnimator.currentPlayTime = 0
                        }
                    }
                }
                launch {
                    //封面
                    viewModel.coverUrl.collect { url ->
                        //当网络请求成功url有值的时候，这里的代码才会被触发！
                        if (!url.isNullOrEmpty()) {
                            Glide.with(this@PlayerFragment)
                                .load(url)
                               // .transform(RoundedCorners(30))
                                .into(binding.ivAlbumCover)

                            coverUrl = url

                        } else {
                            Log.d("hyj", "播放器封面链接还是空的！")
                        }
                    }
                }
                launch {
                    //获取歌词 —— 解析后传给 LyricView
                    viewModel.lyricList.collect { lyrics ->
                        if (lyrics.isNullOrEmpty()) return@collect
                        binding.lvLyrics.setLyrics(lyrics)
                    }

                }
                launch {
                    viewModel.isLiked.collect { bool ->
                        //为是否喜欢赋值
                        isLike = bool
                        if (isLike) {
                            binding.btnFavorite.setImageResource(R.drawable.like1)
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
                        }

                    }
                }
                launch {
                    PlayerManager.isPlaying.collect { isPlaying ->
                        // 更新播放/暂停图标
                        binding.btnPlay.setImageResource(
                            if (isPlaying) R.drawable.ic_pause1 else R.drawable.ic_play
                        )
                        //根据真实的播放状态，控制封面的旋转！
                        if (isPlaying) {
                            //如果正在播放，就让它转
                            if (rotateAnimator.isPaused) {
                                rotateAnimator.resume()
                            } else if (!rotateAnimator.isRunning) {
                                rotateAnimator.start()
                            }
                        } else {
                            //如果暂停了，就让它停在原地
                            if (rotateAnimator.isRunning) {
                                rotateAnimator.pause()
                            }
                        }
                    }
                }
                launch {
                    PlayerManager.currentPosition.collect { position ->
                        binding.tvCurrentTime.text = viewModel.formatTime(position)
                        //监听进度条
                        val duration = PlayerManager.duration.value
                        if (duration > 0) {
                            binding.seekBar.progress =
                                ((position.toFloat() / duration) * 100).toInt()
                        }
                        // 更新 LyricView 的进度 —— 它会自动高亮当前行
                        binding.lvLyrics.updateProgress(position)
                    }
                }
                launch {
                    // 观察歌手名
                    viewModel.artistName.collect { artist ->
                        binding.tvArtist.text = artist
                    }
                }
                launch {
                    // 观察歌曲名
                    viewModel.songName.collect { name ->
                        songName = name
                        binding.tvSong.text = name
                    }
                }
                launch {
                    PlayerManager.duration.collect { duration ->
                        binding.tvTotalTime.text = viewModel.formatTime(duration)
                    }
                }
            }

//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//
//                PlayerManager.currentSong.collect { song ->
//                    if (song != null) {
//
//                        Log.d("hyj", "大管家切歌了！马上指派ViewModel去请求！ID: ${song.id}")
//                        viewModel.fetchMusicUrl(song.id.toString())
//                        //获取歌曲详情
//                        viewModel.fetchSongDetail(song.id.toString())
//                        //获取歌词
//                        viewModel.fetchLyric(song.id.toString())
//                        //获取是否喜欢
//                        viewModel.checkSongIsLiked(song.id.toString())
//
//                    } else {
//                        Log.d("hyj", "没有歌曲要播放")
//                    }
//                }
//
//            }
//        }
        }
    }

    //显示音质选择对话框
    //需要用到 context，直接放在 UI 层
    //
    //private fun showQualityDialog() {
//        AlertDialog.Builder(requireContext())
//            .setTitle("选择音质")
//            .setSingleChoiceItems(qualityOptions, currentQualityIndex) { dialog, which ->
//                currentQualityIndex = which
//                binding.tvQuality.text = qualityOptions[which]
//                dialog.dismiss()
//                ToastUtil.popToastLong("已切换到${qualityOptions[which]}", requireContext())
//
//                //解封这段代码：真正触发网络请求去拿新的音质链接！
//                if (id.isNotEmpty()) {
//                    // qualityLevels[which] 会取出你在底部定义的 "standard", "higher", "exhigh" 等对应英文参数
//                    viewModel.fetchMusicUrl(id, qualityLevels[which])
//                } else {
//                    ToastUtil.popToast("当前没有正在播放的歌曲", requireContext())
//                }
//            }
//            .setNegativeButton("取消", null)
//            .show()
//    }

    override fun onDestroyView() {
        //防止内存泄漏
        rotateAnimator.cancel()
        rotateAnimator.removeAllListeners()
        rotateAnimator.target = null
        _binding?.seekBar?.setOnSeekBarChangeListener(null)
        _binding?.ivAlbumCover?.setOnClickListener(null)
        super.onDestroyView()
    }

    private fun showQualityDialog() {
        val qualityDialog = QualityBottomSheet()
        //接收用户从弹窗里选好的音质
        qualityDialog.onQualitySelected = { level, name ->
            //更新 UI 上的文字
            binding.tvQuality.text = name
            if (name == "臻品音质") binding.tvQuality.setTextColor("#BDE39F".toColorInt())
            else binding.tvQuality.setTextColor("#1C1C1E".toColorInt())
            ToastUtil.popToastLong("已切换到 $name", requireContext())

            //重新请求播放链接
            if (id.isNotEmpty()) {
                viewModel.fetchMusicUrl(id, level)
            }
        }
        //显示弹窗
        qualityDialog.show(childFragmentManager, "QualityDialogTag")
    }

    private fun initRotateAnimation() {
        rotateAnimator = ObjectAnimator.ofFloat(binding.ivAlbumCover, "rotation", 0f, 360f).apply {
            duration = 40000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }
}