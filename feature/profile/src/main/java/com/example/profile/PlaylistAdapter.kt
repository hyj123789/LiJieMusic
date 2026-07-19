package com.example.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.model.UserManager
import com.example.profile.databinding.ItemProfilePlaylistBinding
import com.example.profile.model.playlist.Playlist

class PlaylistAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<Playlist, PlaylistAdapter.ViewHolder>(PlaylistDiffCallback()) {
    private var isLimited: Boolean = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("ljh", "onCreateViewHolder执行了")
        val binding =
            ItemProfilePlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("ljh", "onBindViewHolder执行了")
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        val initCount = super.getItemCount()
        if (isLimited) {
            return if (initCount <= 5) initCount else 5
        } else return initCount
    }

    fun modifyLimited() {
        isLimited = isLimited.not()
    }

    fun getLimited(): Boolean {
        return isLimited
    }

    inner class ViewHolder(private val binding: ItemProfilePlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    currentItem?.let {
                        onItemClick(it.id.toString())
                    }
                }
            }
        }

        private var currentItem: Playlist? = null
        fun bind(item: Playlist) {
            currentItem = item
            binding.tvPlaylistName.text = item.name
            binding.tvPlaylistDescription.text =
                "歌单|${item.trackCount}首|${UserManager.profile.value?.nickname ?: "null"}"
            Glide.with(binding.root.context).load(item.coverImgUrl).into(binding.ivCover)
            Log.d(
                "ljh",
                "绑定成功了，歌单|${item.playCount}首|${UserManager.profile.value?.nickname ?: "null"}" + item.name
            )
        }

    }
}

class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(
        p0: Playlist,
        p1: Playlist
    ): Boolean {
        return p0.id == p1.id
    }

    override fun areContentsTheSame(
        p0: Playlist,
        p1: Playlist
    ): Boolean {
        return p0 == (p1)
    }
}