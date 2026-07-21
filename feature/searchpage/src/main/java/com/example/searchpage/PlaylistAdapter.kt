package com.example.searchpage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaylistAdapter(
    private val onItemClick: (Long) -> Unit = {}
) : ListAdapter<PlaylistItem, PlaylistAdapter.ViewHolder>(PlaylistDiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.img)
        val tvMainTitle: TextView = itemView.findViewById(R.id.tv_main_title)
        val tvSubTitle: TextView = itemView.findViewById(R.id.tv_sub_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_playlist,
            parent,
            false
        )
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = getItem(position)

        //取第一个tag作为主标题
        val mainTag = playlist.tags?.firstOrNull() ?: "精选推荐"
        holder.tvMainTitle.text = mainTag

        //副标题
        holder.tvSubTitle.text = playlist.name

        Glide.with(holder.itemView.context)
            .load(playlist.coverImgUrl)
            .into(holder.ivCover)

        holder.itemView.setOnClickListener {
            onItemClick(playlist.id)
        }
    }

    class PlaylistDiffCallback : DiffUtil.ItemCallback<PlaylistItem>() {

        override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
            return oldItem == newItem
        }
    }

}