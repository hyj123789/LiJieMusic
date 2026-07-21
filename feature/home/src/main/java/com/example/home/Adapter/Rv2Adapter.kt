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
import com.example.home.model.PlaylistInfo2

class Rv2Adapter(
    private val onItemClick: (Long) -> Unit = {}
) : ListAdapter<PlaylistInfo2, Rv2Adapter.ViewHolder>(PlaylistInfo2DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tv2Title.text = item.name

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.img2)

        holder.itemView.setOnClickListener {
            onItemClick(item.id)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img2: ImageView = itemView.findViewById(R.id.iv2)
        val tv2Title: TextView = itemView.findViewById(R.id.tv2MainTitle)
    }

    class PlaylistInfo2DiffCallback : DiffUtil.ItemCallback<PlaylistInfo2>() {

        override fun areItemsTheSame(oldItem: PlaylistInfo2, newItem: PlaylistInfo2): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: PlaylistInfo2, newItem: PlaylistInfo2): Boolean {
            return oldItem == newItem
        }
    }
}