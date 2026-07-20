package com.example.home

import RV4Adapter
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base.BaseFragment
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
    //获取Rv2的Adapter
    private val RV1Adapter = Rv1Adapter()

    //获取Rv2的Adapter
    private val RV2Adapter = Rv2Adapter()
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

}
