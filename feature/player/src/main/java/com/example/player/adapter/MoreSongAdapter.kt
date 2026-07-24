package com.example.player.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.player.databinding.ItemMoreBinding
import com.example.player.model.MoreSongItem

class MoreSongAdapter(
    private val onItemClick: (MoreSongItem) -> Unit
) : ListAdapter<MoreSongItem, MoreSongAdapter.SongViewHolder>(MoreSongDiffCallback()) {

    inner class SongViewHolder(private val binding: ItemMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            //监听点击事件，将点击的歌曲对象回调给 Fragment
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(song: MoreSongItem) {
            //绑定歌名，如果为空则显示默认文本
            binding.tvMoresong.text = song.name ?: "未知歌曲"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemMoreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MoreSongDiffCallback : DiffUtil.ItemCallback<MoreSongItem>() {
    override fun areItemsTheSame(oldItem: MoreSongItem, newItem: MoreSongItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MoreSongItem, newItem: MoreSongItem): Boolean {
        return oldItem == newItem
    }
}