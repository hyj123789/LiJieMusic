package com.example.playlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist.databinding.ItemSongBinding
import com.example.playlist.model.Track

class SongAdapter : ListAdapter<Track, SongAdapter.ViewHolder>(SongDiffCallback()){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d("ljh","onCreateViewHolder方法执行了")
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        Log.d("ljh","onBindViewHolder方法执行了")
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Track){
            binding.apply {
                Glide.with(binding.root.context).load(item.album?.picUrl).into(ivCover)
                tvSongName.text=item.name
                tvArtistName.text=item.artists?.joinToString(separator = "|") { it.name }
            }
        }
    }
}

class SongDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(
        oldItem: Track,
        newItem: Track
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Track,
        newItem: Track
    ): Boolean {
        return oldItem == newItem
    }
}