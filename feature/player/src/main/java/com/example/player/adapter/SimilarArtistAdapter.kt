package com.example.player.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.player.databinding.ItemSimilarArtistBinding
import com.example.player.model.SimilarArtist

class SimilarArtistAdapter(
    private val onItemClick: (SimilarArtist) -> Unit
) : ListAdapter<SimilarArtist, SimilarArtistAdapter.ArtistViewHolder>(ArtistDiffCallback()) {

    inner class ArtistViewHolder(private val binding: ItemSimilarArtistBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(artist: SimilarArtist){
            binding.tvSimilarsonger.text = artist.name
            Glide.with(binding.root.context)
                .load(artist.picUrl)
                .into(binding.ivArtistBg)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ItemSimilarArtistBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArtistDiffCallback : DiffUtil.ItemCallback<SimilarArtist>() {
        override fun areItemsTheSame(oldItem: SimilarArtist, newItem: SimilarArtist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SimilarArtist, newItem: SimilarArtist): Boolean {
            return oldItem == newItem
        }
    }
}