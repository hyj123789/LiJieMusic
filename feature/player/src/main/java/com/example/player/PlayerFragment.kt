package com.example.player

import PlaylistBottomSheet
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.base.BaseFragment
import com.example.base.PlayerManager
import com.example.model.UserManager
import com.example.player.databinding.FragmentPlayerBinding
import com.example.player.model.LyricUtil
import com.example.therouter.RoutePath
import com.example.util.ToastUtil
import kotlinx.coroutines.launch
import com.therouter.router.Route
import androidx.core.graphics.toColorInt


@Route(path = RoutePath.PLAYER_MAIN)
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(FragmentPlayerBinding::inflate) {
    //获取用户id
    val currentUid = UserManager.profile.value?.userId.toString()

    //让外面的小播放器也可以使用大播放器的viewmodel
    private val viewModel: PlayerViewModel by activityViewModels()

    private val qualityOptions = arrayOf("臻品母带", "臻品全景音", "臻品音质")
    private var currentQualityIndex = 0

    //要传输的数据歌曲的id
    var id = ""
    //歌曲名字
    var songname  = ""
    //封面
    var coverurl = ""

    //判断是否喜欢
    var Islike : Boolean  = false
    //是否拖动
    private var isUserSeeking = false

    //初始化歌词adpter
    private val lyricAdapter = LyricAdapter()

    override fun initView() {
        super.initView()
        // 初始化 UI 状态
        binding.tvQuality.text = qualityOptions[currentQualityIndex]
        // 初始化歌词的 RecyclerView
        binding.rvLyrics.adapter = lyricAdapter
        binding.rvLyrics.layoutManager = LinearLayoutManager(requireContext())

        //点击中间区域，切换封面和歌词的显示
        binding.flCenterContent.setOnClickListener {
            if (binding.rvLyrics.visibility == View.VISIBLE) {
                binding.rvLyrics.visibility = View.GONE
                binding.ivAlbumCover.visibility = View.VISIBLE
                binding.LayoutSong.visibility = View.VISIBLE
            } else {
                binding.rvLyrics.visibility = View.VISIBLE
                binding.ivAlbumCover.visibility = View.GONE
                binding.LayoutSong.visibility = View.GONE
                scrollToCurrentLyric()
            }
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

        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                //如果是用户在拖动，实时更新左边的文字时间，让用户知道拖到哪了
                if (fromUser) {
                    val duration = PlayerManager.duration.value
                    val seekPosition = (duration * progress) / 100
                    binding.tvCurrentTime.text = viewModel.formatTime(seekPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
                //正在拖动就不刷新UI
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                //手指松开正式指挥播放器跳转到松开的位置
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
            //实例化底部菜单
            val playlistDialog = PlaylistBottomSheet()
            playlistDialog.show(childFragmentManager, "PlaylistDialogTag")
            ToastUtil.popToast("播放列表功能加载中", requireContext())
        }
    }

    override fun initObservers() {
        super.initObservers()

        //播放音乐
        viewModel.currentSong.observe(viewLifecycleOwner) { songData ->
            if (songData != null && !songData.url.isNullOrEmpty()) {
//
//                Log.d("hyj", "网络请求大功告成，拿到了歌曲URL，准备出声: ${songData.url}")
//
//                PlayerManager.startPlayEngine(songData.id.toString(), songData.url)
                id = songData.id.toString()
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

        //封面
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

        //获取歌词
        viewModel.lyricData.observe(viewLifecycleOwner) { lrcString ->
            if (!lrcString.isNullOrEmpty()) {
                //不是空音乐
                val parsedList = LyricUtil.parseLyric(lrcString)
                lyricAdapter.submitList(parsedList)

            } else {
                //如果是空音乐
                lyricAdapter.submitList(emptyList())
            }
        }

        // 观察播放状态
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                PlayerManager.isPlaying.collect { isPlaying ->
                    // 更新播放/暂停图标
                    binding.btnPlay.setImageResource(
                        if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    )
                }
            }
        }
        // 观察当前播放时间
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                PlayerManager.currentPosition.collect { position ->
                    binding.tvCurrentTime.text = viewModel.formatTime(position)
                    //监听进度条
                    val duration = PlayerManager.duration.value
                    if (duration > 0) {
                        binding.seekBar.progress = ((position.toFloat() / duration) * 100).toInt()
                    }
                    //获取目标行
                    val targetLine = lyricAdapter.updateTime(position)
                    //只要不为-1
                    if (binding.rvLyrics.visibility == View.VISIBLE && targetLine != -1) {
                        //开始滚动
                        binding.rvLyrics.smoothScrollToPosition(targetLine)
                    }
                }
            }
        }

        // 观察总时长
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                PlayerManager.duration.collect { duration ->
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                //要用launch单独包裹不然会被阻塞
                launch {
                    viewModel.isLiked.collect { isLike ->
                        //为是否喜欢赋值
                        Islike = isLike
                        if (isLike) {
                            binding.btnFavorite.setImageResource(R.drawable.like1)
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
                        }

                    }
                }
            }
        }
    }


    private fun scrollToCurrentLyric() {
        val targetLine = lyricAdapter.getCurrentLineIndex() // 拿到当前行

        if (targetLine >= 0) {
            val layoutManager = binding.rvLyrics.layoutManager as LinearLayoutManager
            // 刚点开的时候，使用带 Offset 的方法瞬间居中，体验最好
            layoutManager.scrollToPositionWithOffset(targetLine, binding.rvLyrics.height / 2)
        }
    }


    /**
     * 显示音质选择对话框
     * 需要用到 context，直接放在 UI 层
     */
//    private fun showQualityDialog() {
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
        super.onDestroyView()
    }

    private fun showQualityDialog() {
        val qualityDialog = QualityBottomSheet()
        //接收用户从弹窗里选好的音质
        qualityDialog.onQualitySelected = { level, name ->
            //更新 UI 上的文字
            binding.tvQuality.text = name
            if(name == "臻品音质") binding.tvQuality.setTextColor("#BDE39F".toColorInt())
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
}
