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
import com.example.home.model.PlaylistInfo1

class Rv1Adapter : ListAdapter<PlaylistInfo1, Rv1Adapter.ViewHolder>(PlaylistInfo1DiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.iv1)
        val tv1Title: TextView = itemView.findViewById(R.id.tv1MainTitle)
        val tv1dc : TextView = itemView.findViewById(R.id.tv1SubTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tv1Title.text = item.name
        holder.tv1dc.text = item.description

        Glide.with(holder.itemView.context)
            .load(item.coverImgUrl)
            .into(holder.ivCover)
    }

    class PlaylistInfo1DiffCallback : DiffUtil.ItemCallback<PlaylistInfo1>() {

        override fun areItemsTheSame(oldItem: PlaylistInfo1, newItem: PlaylistInfo1): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: PlaylistInfo1, newItem: PlaylistInfo1): Boolean {
            return oldItem == newItem
        }
    }
}