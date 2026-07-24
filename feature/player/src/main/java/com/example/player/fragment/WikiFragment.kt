package com.example.player.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.base.BaseFragment
import com.example.base.PlayerManager
import com.example.model.UserManager
import com.example.player.PlayerViewModel
import com.example.player.adapter.SimilarArtistAdapter
import com.example.player.adapter.SimilarSongAdapter
import com.example.player.databinding.FragmentWikiBinding
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

class WikiFragment : BaseFragment<FragmentWikiBinding>(FragmentWikiBinding::inflate) {

    val viewModel: PlayerViewModel by activityViewModels()
    val userUrl = UserManager.profile.value?.avatarUrl

    private val similarSongAdapter = SimilarSongAdapter{    song ->
        PlayerManager.playSong(song.id.toString(),song.name?:"未知歌名",song.artists?.get(0)?.name?:"未知歌手")

    }

    private val similarArtistAdapter = SimilarArtistAdapter{

    }

    override fun initView() {
        super.initView()

        Glide.with(requireContext())
            .load(userUrl)
            .into(binding.ivUserAvatar)

        binding.rvSamesong.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSamesong.adapter = similarSongAdapter

        binding.rvSamesonger.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvSamesonger.adapter = similarArtistAdapter

        val snapHelper = LinearSnapHelper()
        binding.rvSamesonger.onFlingListener = null // 防止重复绑定报错
        snapHelper.attachToRecyclerView(binding.rvSamesonger)

        //添加滑动监听,实现“缩小并从左上角消失”的特效
        binding.rvSamesonger.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val childCount = recyclerView.childCount
                for (i in 0 until childCount) {
                    val child = recyclerView.getChildAt(i) ?: continue

                    //获取卡片左边缘相对于屏幕的位置
                    val left = child.left

                    if (left < 0) {
                        //卡片正在向左滑出屏幕
                        //计算滑出的进度，从 0.0 (刚接触边缘) 到 1.0 (完全滑出)
                        val progress = min(1f, abs(left).toFloat() / child.width)

                        //逐渐缩小
                        val scale = 1f - (0.7f * progress)
                        child.scaleX = scale
                        child.scaleY = scale

                        //向上移动 (配合列表本身的向左滑动，形成左上角消失的视觉)
                        //移动的最大距离是卡片自身的高度
                        child.translationY = -(child.height * progress)

                        //逐渐变透明
                        child.alpha = 1f - progress

                    } else {
                        //卡片在屏幕内或即将从右侧滑入，重置为正常状态
                        child.scaleX = 1f
                        child.scaleY = 1f
                        child.translationY = 0f
                        child.alpha = 1f
                    }
                }
            }
        })



        //滑动冲突
        binding.rvSamesonger.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: android.view.MotionEvent): Boolean {
                when (e.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        //当手指在当前列表上按下时强行剥夺 ViewPager2 的事件拦截权
                        rv.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        //当手指抬起或取消滑动时把拦截权还给 ViewPager2
                        rv.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                //必须返回 false，表示我们只做通知，不消费这个事件，让RV自己去处理滑动
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.wikiData.collect { data ->
                        if (data == null) return@collect

                        data.songInfoDto?.songName?.let { songName ->
                            binding.tvWikiTitle.text = "歌曲百科 · $songName"
                        }

                        data.musicFirstListenDto?.let { firstListen ->
                            binding.tvFirstListenDate.text = firstListen.date
                            binding.tvSeason.text = firstListen.season + "的" + firstListen.period
                        }

                        data.musicTotalPlayDto?.let { totalPlay ->
                            binding.tvPlayCount.text = "${totalPlay.playCount}次"
                            binding.tvPlayCompare.text = totalPlay.text
                        }
                    }
                }

                //监听歌曲ID变化
                launch {
                    viewModel.currentSongId.collect { songId ->
                        if (songId != null) {
                            viewModel.loadWikiData(songId)
                            viewModel.fetchSongDetail2(songId.toString())
                            viewModel.fetchSimilarSongDetail(songId.toString())
                        }
                    }
                }

                launch {
                    viewModel.songDetail.collect { data ->
                        if (data != null){
                            val id = data.artistRepVos?.get(0)?.artistId?:0L.toString()
                            Log.d("hyj","访问歌手的id为$id")

                            viewModel.fetchSongerDetail(id.toString())
                            viewModel.fetchSimilarSongerDetail(id.toString())

                            binding.language.text = data.language
                            binding.publishtime.text = ""+viewModel.formatTimestampToDate(data.publishTime ?:0)
                            binding.songsubtitle.text = data.songSubTitle
                            binding.albumname.text = data.albumRepVo?.albumName

                            //提取作词作曲的人
                            var lyricNames = ""
                            val lyricList = data.lyricArtists
                            if (!lyricList.isNullOrEmpty()) {
                                for (i in lyricList.indices) {
                                    val name = lyricList[i].artistName ?: "未知"
                                    lyricNames += name
                                    //如果不是最后一个人就在后面加个 "/"分隔一下
                                    if (i < lyricList.size - 1) {
                                        lyricNames += "/"
                                    }
                                }
                            }
                            var composeNames = ""
                            val composeList = data.composeArtists
                            if (!composeList.isNullOrEmpty()) {
                                for (i in composeList.indices) {
                                    val name = composeList[i].artistName ?: "未知"
                                    composeNames += name
                                    if (i < composeList.size - 1) {
                                        composeNames += "/"
                                    }
                                }
                            }
                            val finalText = "作词 $lyricNames / 作曲 $composeNames"
                            binding.lyricartists.text = finalText
                        }
                    }
                }

                //监听歌手
                launch {
                    viewModel.songer.collect { data ->
                        binding.tvSonger.text = data?.artistName
                        binding.describe.text = data?.desc
                        Glide.with(requireContext())
                            .load(data?.headPicUrl)
                            .into(binding.ivArtistBg)
                    }
                }

                //监听歌曲
                launch {
                    viewModel.similarSong.collect { Song ->
                        similarSongAdapter.submitList(Song)
                    }
                }

                //监听歌手
                launch {
                    viewModel.similarSonger.collect { songer ->
                        similarArtistAdapter.submitList(songer)
                        Log.d("hyj","相似歌手的数据为：${songer.toString()}")
                    }
                }

            }
        }

    }

    override fun initEvent() {
        super.initEvent()
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}