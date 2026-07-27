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
import com.example.searchpage.model.ArtistEntity

class ArtistAdapter(
    private val onItemClick: (ArtistEntity) -> Unit
) : ListAdapter<ArtistEntity, ArtistAdapter.ArtistViewHolder>(ArtistDiffCallback()) {

    inner class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivArtistCover: ImageView = view.findViewById(R.id.ivArtistCover)
        val tvArtistName: TextView = view.findViewById(R.id.tvArtistName)

        fun bind(artist: ArtistEntity) {
            tvArtistName.text = artist.name

            Glide.with(itemView.context)
                .load(artist.picUrl)
                .into(ivArtistCover)

            itemView.setOnClickListener {
                onItemClick(artist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ArtistDiffCallback : DiffUtil.ItemCallback<ArtistEntity>() {
    override fun areItemsTheSame(oldItem: ArtistEntity, newItem: ArtistEntity): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: ArtistEntity, newItem: ArtistEntity): Boolean {
        return oldItem == newItem
    }
}