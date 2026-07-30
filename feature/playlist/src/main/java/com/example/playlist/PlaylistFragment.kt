package com.example.playlist

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.base.Album
import com.example.base.Artist
import com.example.base.BaseFragment
import com.example.base.PlayerManager
import com.example.base.SongDetail
import com.example.playlist.databinding.FragmentPlaylistBinding
import com.example.util.ToastUtil
import kotlinx.coroutines.launch

class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>(FragmentPlaylistBinding::inflate){
    private val viewModel : PlaylistViewModel by viewModels()
    private var currentPlaylistSongs: List<SongDetail> = emptyList()

    private val playlistId: String by lazy {
        arguments?.getString("playlistId") ?: ""
    }
    private val mAdapter by lazy { SongAdapter(playlistId.toLong()) }
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent() // 系统级 API：隐式 Intent 调用系统相册
    ) { uri ->
        uri?.let { viewModel.uploadCover(playlistId.toLong(),it,requireActivity().contentResolver) }
    }

    override fun initView() {
        super.initView()
        binding.rvSongs.apply {
            adapter=mAdapter
            layoutManager= LinearLayoutManager(requireContext())
        }
        val callback = ItemTouchHelperCallback(mAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvSongs)

        mAdapter.OnSongClickListener(object : SongAdapter.OnSongClickListener {
            override fun onSongPlayClick(
                id: String,
                songName: String,
                artistName: String
            ) {
                // 清除旧歌单
                PlayerManager.clearPlaylist()
                //赋值新歌单
                PlayerManager.updatePlaylist(currentPlaylistSongs)
                //播放歌曲
                PlayerManager.playSong(id,songName,artistName)
            }
            override fun onSongNextPlayClick(
                id: String,
                songName: String,
                artistName: String
            ) {
                PlayerManager.addSongToPlaylist(id,songName,artistName)
                ToastUtil.popToast("已添加至列表，下一首播放",requireContext())
            }
            override fun onRemoveSong(pid: Long,ids: String) {
                viewModel.removeSong(pid,ids)
            }

            override fun onToggleSong(pid: Long, ids: String) {
                viewModel.toggleSong(pid,ids)
            }
        })
    }
    override fun initEvent() {
        super.initEvent()
        viewModel.init(playlistId)
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.ivCover.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
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
                            Glide.with(this@PlaylistFragment).load(url).into(binding.ivCover)
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
                launch {
                    viewModel.song.collect { trackList ->
                        //清除歌单
                        val myExtractedSongs = trackList?.map { track ->
                            //取出歌曲列表需要的数据
                            val currentId = track.id
                            val currentName = track.name
                            val firstArtist = track.artists?.firstOrNull()
                            val artistName = firstArtist?.name ?: "未知歌手"

                            //组装成你需要的精简对象
                           SongDetail(
                                id = currentId,
                                name = currentName,
                                ar = listOf(Artist(id = 0L, name = artistName)),
                                al = Album(id = 0L, name = "", picUrl = ""),
                                dt = 0,
                                fee = 0
                            )
                        }
                        currentPlaylistSongs = myExtractedSongs?:emptyList()
                    }
                }
                launch {
                    viewModel.toastMsg.collect { msg->
                        if (msg==null) return@collect
                        ToastUtil.popToast(msg,requireContext())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding?.rvSongs?.adapter = null
        mAdapter.submitList(emptyList())
        super.onDestroyView()
    }
}