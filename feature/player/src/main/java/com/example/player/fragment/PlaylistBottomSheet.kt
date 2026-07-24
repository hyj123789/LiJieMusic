package com.example.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base.PlayMode
import com.example.base.PlayerManager
import com.example.model.UserManager
import com.example.player.adapter.PlaylistAdapter
import com.example.player.R
import com.example.player.databinding.DialogPlaylistBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class PlaylistBottomSheet : BottomSheetDialogFragment() {

    val currentUid = UserManager.profile.value?.userId.toString()
    private var _binding: DialogPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPlaylist.layoutManager = LinearLayoutManager(requireContext())

        playlistAdapter = PlaylistAdapter(
            onItemClick = { song ->
                PlayerManager.playSong(song.id.toString(), song.name, song.al.name)
            },
            onDeleteClick = { song ->
                PlayerManager.removeSong(song)
            }
        )
        binding.rvPlaylist.adapter = playlistAdapter

        //清空所有
        binding.ivdelect.setOnClickListener {
            PlayerManager.clearPlaylist()
        }


        binding.btnPlayMode.setOnClickListener {
            PlayerManager.togglePlayMode()
        }

        initObservers()

    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                //监听列表变化，自动刷新 RecyclerView
                launch {
                    PlayerManager.playlist.collect { list ->
                        playlistAdapter.submitList(list)
                        //更新标题上的数量和正在播放的歌曲
                        binding.tvTitle.text = "当前播放 (${list.size})"
                    }
                }

                //监听正在播放的歌曲，自动让列表里的那首歌变红
                launch {
                    PlayerManager.currentSong.collect { current ->
                        if (current != null) {
                            playlistAdapter.updateCurrentPlaying(current.id)
                        }
                    }
                }

                launch {
                    PlayerManager.playlist.collect { songs ->
                        playlistAdapter.submitList(songs)

                    }
                }

                launch {
                    PlayerManager.playMode.collect { model ->
                        when(model){
                            PlayMode.SEQUENTIAL-> binding.btnPlayMode.setImageResource(R.drawable.ic_shunxun)
                            PlayMode.SHUFFLE -> binding.btnPlayMode.setImageResource(R.drawable.ic_shuiji)
                            PlayMode.SINGLE_LOOP -> binding.btnPlayMode.setImageResource(R.drawable.ic_danqu)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}