package com.example.home

import RV4Adapter
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
import androidx.core.net.toUri

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate){

    //获取viewmodel
    private val viewModel: HomeViewModel by viewModels()
    //获取所有的Adapter
    private val rv1Adapter = Rv1Adapter { playlistId ->
        navigateToPlaylist(playlistId)
    }

    private val rv2Adapter = Rv2Adapter { playlistId ->
        navigateToPlaylist(playlistId)
    }
    private val rv3Adapte = Rv3Adapte()
    private val rv4Adapter = RV4Adapter()

    override fun initView() {
        //榜定rv1
        binding.rv1.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        binding.rv1.adapter = rv1Adapter
        //榜定rv2
        binding.rv2.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        binding.rv2.adapter = rv2Adapter
        //榜定rv3
        val gridLayoutManager3 = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.HORIZONTAL,
            false
        )
        binding.rv3.layoutManager = gridLayoutManager3
        binding.rv3.adapter = rv3Adapte
        //绑定Rv4
        //定义网格布局
        val gridLayoutManager4 = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.HORIZONTAL,
            false
        )
        binding.rv4.layoutManager = gridLayoutManager4
        binding.rv4.adapter = rv4Adapter

        //使唤ViewModel去请求数据
        viewModel.fetchRecommendPlaylists()
    }


    override fun initEvent() {
        super.initEvent()
        binding.btnDrawer.setOnClickListener {
            (activity as? DrawerUtil)?.openDrawer()
        }
        //跳往搜索页面
        binding.imgtosearch.setOnClickListener {
            //对于这个暗号进行访问
            val request = NavDeepLinkRequest.Builder
                .fromUri("musicapp://search_page".toUri())
                .build()
            findNavController().navigate(request)

        }
        //rv3rv4点击事件的书写
        rv3Adapte.OnSongClickListener3(object : Rv3Adapte.OnSongClickListener {
            override fun onSongPlayClick(
                id: String,
                songName: String,
                artistName: String
            ) {
                PlayerManager.playSong(id,songName,artistName)
            }
            override fun onSongNextPlayClick(
                id: String,
                songName: String,
                artistName: String
            ) {
                PlayerManager.addSongToPlaylist(id,songName,artistName)
            }
        })

        rv4Adapter.setOnSongClickListener(object : RV4Adapter.OnSongClickListener {
            override fun onSongPlayClick(
                id: String,
                songName: String,
                artistName: String
            ) {
                PlayerManager.playSong(id,songName,artistName)
            }
            override fun onSongNextPlayClick(
                id: String,
                songName: String,
                artistName: String
            ) {
                PlayerManager.addSongToPlaylist(id,songName,artistName)
            }
        })
    }

     override fun initObservers() {
         viewLifecycleOwner.lifecycleScope.launch {
             //只有在页面可见时才监听，不可见就没有必要监听
             repeatOnLifecycle(Lifecycle.State.STARTED) {

                 viewModel.playlistFlow1
                     .onEach { realData ->
                         //只要数据不是空的就给Rv1的Apdater配置数据
                         if (realData.isNotEmpty()) {
                             rv1Adapter.submitList(realData)
                         }
                     }
                     .launchIn(this) //把任务交给当前repeatOnLifecycle所在的协程去后台跑

                 viewModel.playlistFlow2
                     .onEach { realData ->
                         if (realData.isNotEmpty()) {
                             rv2Adapter.submitList(realData)
                         }
                     }
                     .launchIn(this)

                 viewModel.playlistFlow3
                     .onEach { realData ->
                         if (realData.isNotEmpty()) {
                             rv3Adapte.submitList(realData)
                         }
                     }
                     .launchIn(this)

                 viewModel.playlistFlow4
                     .onEach { realData ->
                         if (realData.isNotEmpty()) {
                             rv4Adapter.submitList(realData)
                         }
                     }
                     .launchIn(this)
             }
         }
    }

    override fun onDestroyView() {
        _binding?.rv1?.adapter = null
        _binding?.rv2?.adapter = null
        _binding?.rv3?.adapter = null
        _binding?.rv4?.adapter = null
        super.onDestroyView()
    }

    private fun navigateToPlaylist(playlistId: Long) {
        val request = NavDeepLinkRequest.Builder
            .fromUri("musicapp://playlist/$playlistId".toUri())
            .build()
        findNavController().navigate(request)
    }
}
