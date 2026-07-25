package com.example.video.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.video.databinding.ItemAllMvBinding
import com.example.video.model.DataX

class AllMvAdapter(private val onItemClick :(Long)-> Unit) : PagingDataAdapter<DataX, AllMvAdapter.ViewHolder>(DataXDiffCallback()) {
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
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemAllMvBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                onItemClick.invoke(binding.ivItemAllMvCover.tag as? Long ?: 0L)
            }
        }
        fun bind(item: DataX){
            binding.ivItemAllMvCover.tag = item.id.toLong()
            // 指定固定宽高加载图片，避免 Glide 加载全尺寸大图
            Glide.with(binding.root.context)
                .load(item.cover)
                .transform(CenterCrop())
                .override(360, 180)
                .into(binding.ivItemAllMvCover)
            if (item.briefDesc.isNullOrEmpty()){
                binding.tvItemAllMvDesc.text = item.name
            } else {
                binding.tvItemAllMvDesc.text = "${item.name} | ${item.briefDesc}"
            }
            binding.tvItemMvCounts.visibility = View.GONE
            binding.tvItemMvDetail.visibility = View.GONE
        }
    }
}

class DataXDiffCallback : DiffUtil.ItemCallback<DataX>() {
    override fun areItemsTheSame(
        oldItem: DataX,
        newItem: DataX
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: DataX,
        newItem: DataX
    ): Boolean {
        return oldItem == newItem
    }
}