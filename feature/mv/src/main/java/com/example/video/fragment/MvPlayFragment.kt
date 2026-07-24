package com.example.video.fragment

import android.content.res.Configuration
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.base.BaseFragment
import com.example.video.VideoViewModel
import com.example.video.databinding.FragmentMvPlayBinding
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import kotlinx.coroutines.launch

class MvPlayFragment : BaseFragment<FragmentMvPlayBinding>(FragmentMvPlayBinding::inflate) {
    val id : Long by lazy { requireArguments().getString("mvId")?.toLong() ?: 0L }
    private val viewModel : VideoViewModel by viewModels()
    private var orientationUtils: OrientationUtils? = null

    override fun initView() {
        super.initView()
        // 旋转感应（物理旋转时自动横竖屏）
        orientationUtils = OrientationUtils(requireActivity(), binding.videoPlayer).apply {
            setEnable(true)
        }
        // GSYVideoPlayer v9 全屏按钮没有内置逻辑，手动绑定
        binding.videoPlayer.fullscreenButton.setOnClickListener {
            Log.d("ljh", "====== 点击了全屏按钮 ======")
            if (binding.videoPlayer.isIfCurrentIsFullscreen) {
                Log.d("ljh", "当前已全屏，退出全屏")
                binding.videoPlayer.onBackFullscreen()
            } else {
                Log.d("ljh", "当前非全屏，调用 startWindowFullscreen")
                val result = binding.videoPlayer.startWindowFullscreen(requireActivity(), true, false)
                Log.d("ljh", "startWindowFullscreen 返回: $result")
            }
        }
    }

    override fun initEvent() {
        super.initEvent()
        Log.d("ljh","拿到MVid"+id)
        viewModel.fetchMvUrl(id)
        viewModel.fetchMvDetail(id)
    }

    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.mvDetail.collect { detailRes ->
                        detailRes?.apply {
                            binding.tvLike.text=detailRes.likedCount.toString()
                            binding.tvShare.text=detailRes.shareCount.toString()
                            binding.tvComment.text=detailRes.commentCount.toString()
                        } ?: return@collect
                    }
                }
                launch {
                    viewModel.mvUrl.collect { url->
                        url?.apply{
                            binding.videoPlayer.setUp(url,true,"MV")
                            binding.videoPlayer.startPlayLogic()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        binding.videoPlayer.onVideoResume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 横竖屏切换时转发给播放器，保证全屏窗口状态同步
        orientationUtils?.let {
            binding.videoPlayer.onConfigurationChanged(requireActivity(), newConfig, it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        orientationUtils?.releaseListener()
    }
}
