package com.example.video.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.video.databinding.ItemAllMvBinding
import com.example.video.model.DataTop
import com.example.video.model.DataX

class TopMvAdapter(private val onItemClick : (Long) -> Unit) : ListAdapter<DataTop, TopMvAdapter.ViewHolder>(DataTopDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemAllMvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
        holder.currentId = getItem(position).id.toLong()
    }

    inner class ViewHolder(private val binding: ItemAllMvBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick.invoke(currentId)
            }
        }
        var currentId = 0L
        fun bind(item: DataTop) {
            Glide.with(binding.root.context).load(item.cover).into(binding.ivItemAllMvCover)
            if (item.briefDesc.isNullOrEmpty()) {
                binding.tvItemAllMvDesc.text = item.name
            } else binding.tvItemAllMvDesc.text = item.name + "|" + item.briefDesc
            binding.tvItemMvDetail.text = item.artists.joinToString(separator = ",") {artist ->
                artist.name
            }
            binding.tvItemMvCounts.text = "${ item.playCount }次播放"
        }
    }
}

class DataTopDiffCallback : DiffUtil.ItemCallback<DataTop>() {
    override fun areItemsTheSame(
        oldItem: DataTop,
        newItem: DataTop
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: DataTop,
        newItem: DataTop
    ): Boolean {
        return oldItem == newItem
    }

}