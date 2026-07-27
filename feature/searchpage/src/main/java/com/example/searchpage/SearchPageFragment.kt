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
}