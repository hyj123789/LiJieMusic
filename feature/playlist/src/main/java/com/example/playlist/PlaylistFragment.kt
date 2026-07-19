package com.example.playlist

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.base.BaseFragment
import com.example.playlist.databinding.FragmentPlaylistBinding
import kotlinx.coroutines.launch

class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>(FragmentPlaylistBinding::inflate){
    private val viewModel : PlaylistViewModel by viewModels()
    private val mAdapter = SongAdapter()
    private val playlistId: String by lazy {
        arguments?.getString("playlistId") ?: ""
    }
    override fun initView() {
        super.initView()
        binding.rvSongs.apply {
            adapter=mAdapter
            layoutManager= LinearLayoutManager(requireContext())
        }
    }
    override fun initEvent() {
        super.initEvent()
        viewModel.init(playlistId)
    }

    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.rvList.collect { rvList->
                        rvList?.apply {
                            val newList = rvList.toMutableList()
                            mAdapter.submitList(newList)
                        } ?: return@collect
                    }
                }
                launch {
                    viewModel.coverUrl.collect { url->
                        url?.apply {
                            Glide.with(requireContext()).load(url).into(binding.ivCover)
                        } ?: return@collect
                    }
                }
                launch {
                    viewModel.name.collect { name->
                        name?.apply {
                            binding.tvPlaylist.text=name
                        } ?: return@collect
                    }
                }
                launch {
                    viewModel.songCounts.collect { counts->
                        counts?.apply {
                            binding.tvCounts.text=counts
                        } ?: return@collect
                    }
                }
            }
        }
    }
}