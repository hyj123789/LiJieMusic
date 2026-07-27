package com.example.searchpage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.searchpage.R
import com.example.searchpage.model.AlbumEntity

class AlbumAdapter(
    private val onItemClick: (AlbumEntity) -> Unit
) : ListAdapter<AlbumEntity, AlbumAdapter.AlbumViewHolder>(AlbumDiffCallback()) {

    inner class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAlbumCover: ImageView = view.findViewById(R.id.ivAlbumCover)
        val tvAlbumName: TextView = view.findViewById(R.id.tvAlbumName)
        val tvArtistName: TextView = view.findViewById(R.id.tvArtistName)

        fun bind(album: AlbumEntity) {
            tvAlbumName.text = album.name
            tvArtistName.text = album.artist?.name ?: "未知歌手"

            Glide.with(itemView.context)
                .load(album.picUrl)
                .placeholder(android.R.color.darker_gray)
                .into(ivAlbumCover)

            itemView.setOnClickListener { onItemClick(album) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class AlbumDiffCallback : DiffUtil.ItemCallback<AlbumEntity>() {
    override fun areItemsTheSame(oldItem: AlbumEntity, newItem: AlbumEntity): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: AlbumEntity, newItem: AlbumEntity): Boolean {
        return oldItem == newItem
    }
}