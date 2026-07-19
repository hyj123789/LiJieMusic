package com.example.lijiemusic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.base.BaseActivity
import com.example.lijiemusic.core.navigation.RoutePath
import com.example.lijiemusic.databinding.ActivityMainBinding
import com.example.lijiemusic.databinding.HeadLayoutBinding
import com.example.model.UserManager
import com.example.player.MediaControllerHelper
import com.example.player.PlayerViewModel
import com.example.util.DrawerUtil
import com.example.util.ToastUtil
import com.therouter.router.Route
import kotlinx.coroutines.launch

@Route(path = RoutePath.MAIN_ACTIVITY)
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), DrawerUtil{

    //调用大播放器的viewmodel
    private val viewModel: PlayerViewModel by viewModels()
    private var _headBinding : HeadLayoutBinding? =null
    private val headBinding get() = _headBinding!!

    //声明一个用来控制底层播放的 Helper
    private var mediaControllerHelper: MediaControllerHelper? = null

    override fun initView() {
        super.initView()
        initNav()
        val headerView = binding.navDrawer.getHeaderView(0)
        _headBinding = HeadLayoutBinding.bind(headerView)

        headBinding.ivDrawerAvatar
        lifecycleScope.launch{
            UserManager.profile.collect { profile ->
                profile?.apply {
                    Glide.with(this@MainActivity).load(profile.avatarUrl).into(headBinding.ivDrawerAvatar)
                    headBinding.tvDrawerUsername.text=profile.nickname
                }
            }
        }
    }

    override fun initEvent() {
        super.initEvent()
        binding.navDrawer.setNavigationItemSelectedListener { menuItem ->
            ToastUtil.popToast("后端没给接口哇~~~呜呜呜",this)
            menuItem.isChecked = true
            binding.drawerlayout.closeDrawers()
            true
        }
    }

    private fun initNav(){
        //navigation的相关配置
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)

        binding.layoutMiniPlayer.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.playerFragment)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.playerFragment) {
                binding.layoutMiniPlayer.visibility = View.GONE
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }
        initMiniPlayer()    }
    private fun initMiniPlayer() {

        //初始化 Controller 并连接服务
        mediaControllerHelper = MediaControllerHelper(this, object : MediaControllerHelper.MediaControllerListener {
            override fun onConnected() {}
            override fun onPlayingStateChanged(isPlaying: Boolean) {}
            override fun onDurationChanged(duration: Long) {}
            override fun onPositionChanged(position: Long) {}
            override fun onMediaItemChanged(mediaItem: androidx.media3.common.MediaItem) {}
            override fun onPlaybackEnded() {}
        })
        mediaControllerHelper?.connect()

        //监听歌曲歌名
        viewModel.artistName.observe(this) { name ->
            if (name != null) {
                binding.tvMiniSong.text = name
            }
        }

        //监听封面
        viewModel.coverUrl.observe(this){ cover ->
            Glide.with(this)
                .load(cover)
                .transform(RoundedCorners(16)) //小封面圆角给小一点
                .into(binding.ivMiniCover)

        }

        //监听播放状态变化（更新播放/暂停按钮的图标）
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isPlaying.collect { isPlaying ->
                    if (isPlaying) {
                        binding.ivMiniPlay.setImageResource(R.drawable.play)
                    } else {
                        binding.ivMiniPlay.setImageResource(R.drawable.puse)
                    }
                }
            }
        }
    }

    //新增：页面销毁时，断开连接，防止内存泄漏
    override fun onDestroy() {
        super.onDestroy()
        mediaControllerHelper?.disconnect()
        mediaControllerHelper = null
    }
    override fun openDrawer(){
        binding.drawerlayout.openDrawer(binding.navDrawer)
    }

    override fun closeDrawer() {
        binding.drawerlayout.closeDrawer(binding.navDrawer)
    }
}