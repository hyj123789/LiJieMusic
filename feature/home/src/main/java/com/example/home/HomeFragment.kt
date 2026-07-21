package com.example.home

import RV4Adapter
import android.net.Uri
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base.BaseFragment
import com.example.base.PlayerManager
import com.example.home.Adapter.Rv1Adapter
import com.example.home.Adapter.Rv2Adapter
import com.example.home.Adapter.Rv3Adapte
import com.example.home.databinding.FragmentHomeBinding
import com.example.util.DrawerUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate){

    //获取viewmodel
    private val viewModel: HomeViewModel by viewModels()
    //获取Rv1的Adapter
    private val RV1Adapter = Rv1Adapter { playlistId ->
        navigateToPlaylist(playlistId)
    }

    //获取Rv2的Adapter
    private val RV2Adapter = Rv2Adapter { playlistId ->
        navigateToPlaylist(playlistId)
    }
    //获取Rv3的Adapter
    private val Rv3Adapte = Rv3Adapte()

    private val RV4Adapter = RV4Adapter()

    override fun initView() {
        //榜定rv1
        binding.rv1.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        binding.rv1.adapter = RV1Adapter
        //榜定rv2
        binding.rv2.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        binding.rv2.adapter = RV2Adapter

        //榜定rv3
        val gridLayoutManager3 = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.HORIZONTAL,
            false
        )
        //绑定Rv4
        val gridLayoutManager4 = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.HORIZONTAL,
            false
        )
        binding.rv3.layoutManager = gridLayoutManager3
        binding.rv3.adapter = Rv3Adapte

        binding.rv4.layoutManager = gridLayoutManager4
        binding.rv4.adapter = RV4Adapter


        //使唤ViewModel去请求数据
        viewModel.fetchRecommendPlaylists()

        binding.imgtosearch.setOnClickListener {

            //对于这个暗号进行访问
            val request = NavDeepLinkRequest.Builder
                .fromUri(Uri.parse("musicapp://search_page"))
                .build()

            findNavController().navigate(request)

        }


        testPlayerPipeline()


    }

    // 💡 这是一个专门用来测试的临时方法，可以在 onViewCreated 里调用它
    private fun testPlayerPipeline() {
        // 1. 准备 10 首测试歌曲的假数据 (ID, 歌名, 歌手)
        val mockSongs = listOf(
            Triple("33894312", "海阔天空", "Beyond"),
            Triple("185813", "晴天", "周杰伦"),
            Triple("185850", "七里香", "周杰伦"),
            Triple("25906124", "江南", "林俊杰"),
            Triple("65538", "十年", "陈奕迅"),
            Triple("1901371647", "孤勇者", "陈奕迅"),
            Triple("281951", "后来", "刘若英"),
            Triple("27808044", "平凡之路", "朴树"),
            Triple("139774", "喜欢你", "G.E.M.邓紫棋"),
            Triple("29567189", "泡沫", "G.E.M.邓紫棋")
        )

        Log.d("hyj", "--- 开始执行测试用例 ---")

        // 2. 将这 10 首歌悄悄塞进播放列表
        // 🚨 注意：这里要用咱们之前写好的纯添加方法（只加列表，不改变当前歌曲，不触发网络请求）
        mockSongs.forEach { (id, name, artist) ->
            //添加歌曲
            PlayerManager.addSongToPlaylist(id, name, artist)
        }
        Log.d("hyj", "✅ 10首测试歌曲已成功加入大管家的播放列表！")

        // 3. 模拟用户在列表里点击了第一首歌（海阔天空）
        val targetSong = mockSongs[0]
        Log.d("hyj", "🎵 模拟用户点击，准备播放: ${targetSong.second} - ${targetSong.third}")

        // 🚀 核心动作：调用 changeSong！
        // 这会触发 currentSong 变化 -> 被你的桥梁监听到 -> ViewModel 去请求这首歌的 URL -> 拿到 URL 后自动出声！
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("hyj", "⏳ 等待底层服务连接中...")
            kotlinx.coroutines.delay(5000) // 延迟 2 秒

            val targetSong = mockSongs[0]
            Log.d("hyj", "🎵 服务应该连上了，模拟用户点击: ${targetSong.second}")

            // 触发全自动播放流水线
            //播放歌曲
            PlayerManager.playSong(
                id = targetSong.first,
                songName = targetSong.second,
                artistName = targetSong.third
            )
        }
    }

    override fun initEvent() {
        super.initEvent()
        binding.btnDrawer.setOnClickListener {
            (activity as? DrawerUtil)?.openDrawer()
        }
    }

     override fun initObservers() {
         viewLifecycleOwner.lifecycleScope.launch {

             //只有在页面可见时才监听，不可见就没有必要监听
             repeatOnLifecycle(Lifecycle.State.STARTED) {

                 viewModel.playlistFlow1
                     .onEach { realData ->
                         //只要数据不是空的就给Rv1的Apdater配置数据
                         if (realData.isNotEmpty()) {
                             RV1Adapter.submitList(realData)
                         }
                     }
                     .launchIn(this) //把任务交给当前repeatOnLifecycle所在的协程去后台跑

                 viewModel.playlistFlow2
                     .onEach { realData ->
                         if (realData.isNotEmpty()) {
                             RV2Adapter.submitList(realData)
                         }
                     }
                     .launchIn(this)

                 viewModel.playlistFlow3
                     .onEach { realData ->
                         if (realData.isNotEmpty()) {
                             Rv3Adapte.submitList(realData)
                         }
                     }
                     .launchIn(this)

                 viewModel.playlistFlow4
                     .onEach { realData ->
                         if (realData.isNotEmpty()) {
                             RV4Adapter.submitList(realData)
                         }
                     }
                     .launchIn(this)
             }
         }
    }

    private fun navigateToPlaylist(playlistId: Long) {
        val request = NavDeepLinkRequest.Builder
            .fromUri(Uri.parse("musicapp://playlist/$playlistId"))
            .build()
        findNavController().navigate(request)
    }
}
