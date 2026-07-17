package com.example.home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.home.R
import com.example.home.model.SongItem

class Rv3Adapte : ListAdapter<SongItem, Rv3Adapte.ViewHolder>(SongDiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img3: ImageView = itemView.findViewById(R.id.img3)
        val tv3singer: TextView = itemView.findViewById(R.id.singer)
        val tv3sing : TextView = itemView.findViewById(R.id.sing)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item3, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tv3sing.text = item.ar.firstOrNull()?.name ?: "未知歌手"
        holder.tv3singer.text  = item.name

        Glide.with(holder.itemView.context)
            .load(item.al.picUrl)
            .into(holder.img3)
    }

    class SongDiffCallback : DiffUtil.ItemCallback<SongItem>() {

        override fun areItemsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
            return oldItem == newItem
        }
    }
}