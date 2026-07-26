package com.example.searchpage

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.base.BaseFragment
import com.example.searchpage.databinding.FragmentSearchPageBinding
import com.example.util.DrawerUtil
import kotlinx.coroutines.launch
import kotlin.getValue

class SearchPageFragment : BaseFragment<FragmentSearchPageBinding>(FragmentSearchPageBinding::inflate){

    private val viewModel: SearchPageViewmodel by viewModels()
    private val Adapter = PlaylistAdapter { playlistId ->
        navigateToPlaylist(playlistId)
    }

    override fun initView() {
        binding.rvgedan.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvgedan.adapter = Adapter

        viewModel.fetchRecommendPlaylists()

        val tvFakeSearch = view?.findViewById<TextView>(R.id.tv_fake_search)

        tvFakeSearch?.setOnClickListener {
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
        lifecycleScope.launch {
            viewModel.playListFlow.collect { playlists ->
                if (playlists.isNotEmpty()) {
                    Adapter.submitList(playlists)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding?.rvgedan?.adapter = null
        super.onDestroyView()
    }

    private fun navigateToPlaylist(playlistId: Long) {
        val request = NavDeepLinkRequest.Builder
            .fromUri(Uri.parse("musicapp://playlist/$playlistId"))
            .build()
        findNavController().navigate(request)
    }

}