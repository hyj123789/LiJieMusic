package com.example.searchpage

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.base.BaseFragment
import com.example.searchpage.databinding.FragmentSearchPageBinding
import kotlinx.coroutines.launch
import kotlin.getValue
import androidx.core.net.toUri
import com.example.searchpage.adapter.PlaylistAdapter
import com.example.util.DrawerUtil

class SearchPageFragment : BaseFragment<FragmentSearchPageBinding>(FragmentSearchPageBinding::inflate){

    private val viewModel: SearchPageViewmodel by viewModels()
    private val Adapter = PlaylistAdapter { playlistId ->
        navigateToPlaylist(playlistId)
    }

    override fun initView() {
        binding.rvgedan.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvgedan.adapter = Adapter
        viewModel.fetchRecommendPlaylists()
    }

    override fun initEvent() {
        super.initEvent()

        binding.tvFakeSearch.setOnClickListener {
            //对于这个暗号进行访问
            val request = NavDeepLinkRequest.Builder
                .fromUri("musicapp://search_page".toUri())
                .build()
            findNavController().navigate(request)
        }
        binding.btnDrawer.setOnClickListener {
            (activity as? DrawerUtil)?.openDrawer()
        }
        clickEvent()
    }

    override fun initObservers() {
        lifecycleScope.launch {
            viewModel.playListFlow.collect { playlists ->
                if (playlists.isNotEmpty()) {
                    Adapter.submitList(playlists)
                }
            }
        }
    }

    private fun navigateToPlaylist(playlistId: Long) {
        val request = NavDeepLinkRequest.Builder
            .fromUri("musicapp://playlist/$playlistId".toUri())
            .build()
        findNavController().navigate(request)
    }

    override fun onDestroyView() {
        _binding?.rvgedan?.adapter = null
        super.onDestroyView()
    }

    fun clickEvent(){
        binding.imgRank.setOnClickListener {
            val bottomSheet = WheelMenuBottomSheet(0)
            bottomSheet.show(childFragmentManager, "WheelMenu")
        }

        binding.imgSinger.setOnClickListener {
            val bottomSheet = WheelMenuBottomSheet(1)
            bottomSheet.show(childFragmentManager, "WheelMenu")
        }

        binding.imgGenre.setOnClickListener {
            val bottomSheet = WheelMenuBottomSheet(2)
            bottomSheet.show(childFragmentManager, "WheelMenu")
        }

        binding.imgAlbum.setOnClickListener {
            val bottomSheet = WheelMenuBottomSheet(3)
            bottomSheet.show(childFragmentManager, "WheelMenu")
        }
        binding.imgBook.setOnClickListener {
            val bottomSheet = WheelMenuBottomSheet(4)
            bottomSheet.show(childFragmentManager, "WheelMenu")
        }

    }
}