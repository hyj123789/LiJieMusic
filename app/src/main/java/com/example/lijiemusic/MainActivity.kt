package com.example.lijiemusic

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import com.example.player.fragment.PlaylistBottomSheet
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.base.BaseActivity
import com.example.base.LocalPlaylistManager
import com.example.lijiemusic.databinding.ActivityMainBinding
import com.example.lijiemusic.databinding.HeadLayoutBinding
import com.example.model.UserManager
import com.example.base.MediaControllerHelper
import com.example.base.PlayerManager
import com.example.player.PlayerViewModel
import com.example.therouter.RoutePath
import com.example.util.DrawerUtil
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.launch

@Route(path = RoutePath.MAIN_ACTIVITY)
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), DrawerUtil {


    //调用大播放器的viewmodel
    private val viewModel: PlayerViewModel by viewModels()
    private var _headBinding: HeadLayoutBinding? = null
    private val headBinding get() = _headBinding!!
    private val originalConstraintSet by lazy {
        ConstraintSet().apply {
            clone(binding.main)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    //声明一个用来控制底层播放的 Helper
    private var mediaControllerHelper: MediaControllerHelper? = null

    override fun initView() {
        super.initView()
        initNav()
        val headerView = binding.navDrawer.getHeaderView(0)
        _headBinding = HeadLayoutBinding.bind(headerView)

        lifecycleScope.launch {
            UserManager.profile.collect { profile ->
                profile?.apply {
                    Glide.with(this@MainActivity).load(profile.avatarUrl)
                        .into(headBinding.ivDrawerAvatar)
                    headBinding.tvDrawerUsername.text = profile.nickname
                }
            }
        }
        //取出本地歌单
        val savedPlaylist = LocalPlaylistManager.getPlaylist(this)
        PlayerManager.updatePlaylist(savedPlaylist)
        binding.drawerlayout.setStatusBarBackgroundColor(Color.TRANSPARENT)
    }

    override fun initEvent() {
        super.initEvent()
        binding.navDrawer.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.menu_dynamics -> {
                    TheRouter.build(RoutePath.DYNAMICS_MAIN).navigation()
                }
                R.id.menu_logout ->{
                    showLogoutDialog()
                }
            }
            binding.drawerlayout.closeDrawers()
            true
        }
    }

    private fun initNav() {
        //navigation的相关配置
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)

        binding.ivMiniCover.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.fragment_player_container)
        }

        binding.ivMiniPlaylist.setOnClickListener {
            val playlistDialog = PlaylistBottomSheet()
            playlistDialog.show(supportFragmentManager, "PlaylistDialogTag")
        }
        binding.ivMiniPlay.setOnClickListener {
            PlayerManager.togglePlayPause()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.fragment_player_container || destination.id == R.id.fragment_comment) {
                binding.layoutMiniPlayer.visibility = View.GONE
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                binding.bottomNavView.visibility = View.VISIBLE
            }
            if(destination.id!=R.id.fragment_home&&destination.id!=R.id.fragment_search_page&&destination.id!=R.id.fragment_video&&destination.id!=R.id.fragment_profile){
                binding.bottomNavView.visibility = View.GONE
                binding.layoutMiniPlayer
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.main)
                constraintSet.connect(
                    R.id.layout_mini_player,
                    ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM,
                    5
                )
                constraintSet.applyTo(binding.main)
            } else {
                originalConstraintSet.applyTo(binding.main)
            }
        }
        initMiniPlayer()
        PlayerManager.initPlayer(this)
    }

    private fun initMiniPlayer() {

        //初始化 Controller 并连接服务
        mediaControllerHelper =
            MediaControllerHelper(this, object : MediaControllerHelper.MediaControllerListener {
                override fun onConnected() {}
                override fun onPlayingStateChanged(isPlaying: Boolean) {}
                override fun onDurationChanged(duration: Long) {}
                override fun onPositionChanged(position: Long) {}
                override fun onMediaItemChanged(mediaItem: MediaItem) {}
                override fun onPlaybackEnded() {}
            })
        mediaControllerHelper?.connect()

        //监听歌曲歌名
        viewModel.songName.observe(this) { name ->
            if (name != null) {
                binding.tvMiniSong.text = name
            }
        }

        //监听封面
        viewModel.coverUrl.observe(this) { cover ->
            Glide.with(this)
                .load(cover)
                .transform(RoundedCorners(16)) //小封面圆角给小一点
                .into(binding.ivMiniCover)

        }

        //监听播放状态变化（更新播放/暂停按钮的图标）
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                PlayerManager.isPlaying.collect { isPlaying ->
                    if (isPlaying) {
                        binding.ivMiniPlay.setImageResource(R.drawable.play)
                    } else {
                        binding.ivMiniPlay.setImageResource(R.drawable.pause)
                    }
                }
            }
        }

        //全局监听器：大管家切歌 -> 发起网络请求
        lifecycleScope.launch {
            PlayerManager.currentSong.collect { song ->
                if (song != null) {
                    Log.d("hyj", "【全局】大管家切歌了！指派ViewModel去请求！ID: ${song.id}")
                    viewModel.fetchMusicUrl(song.id.toString())
                    viewModel.fetchSongDetail(song.id.toString())
                    viewModel.fetchLyric(song.id.toString())
                    viewModel.checkSongIsLiked(song.id.toString())
                }
            }
        }

        //全局监听器：拿到 URL -> 启动底层播放器发声
        viewModel.currentSong.observe(this) { songData ->
            if (songData != null && !songData.url.isNullOrEmpty()) {
                val url = songData?.url
                Log.d("hyj", "【全局】拿到歌曲URL，准备出声: ${songData.url}")
                PlayerManager.startPlayEngine(songData.id.toString(), url.toString())
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

    private fun showLogoutDialog(){
        LogoutDialog().show(supportFragmentManager,"logout")
    }

    override fun onStop() {
        super.onStop()

        Log.d("hyj_debug", "==== 触发了 onStop 生命周期 ====")
        val currentPlaylist = PlayerManager.playlist.value

        Log.d("hyj_debug", "准备保存，当前列表歌曲数量: ${currentPlaylist.size}")

        if (currentPlaylist.isNotEmpty()) {
            LocalPlaylistManager.savePlaylist(this, currentPlaylist)
            Log.d("hyj_debug", "==== 保存成功！ ====")
        } else {
            Log.d("hyj_debug", "==== 列表是空的，放弃保存！ ====")
        }
    }
}