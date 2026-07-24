package com.example.video.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.video.databinding.ItemAllMvBinding
import com.example.video.model.DataX

class AllMvAdapter(private val onItemClick :(Long)-> Unit) : ListAdapter<DataX, AllMvAdapter.ViewHolder>(DataXDiffCallback()) {
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
        Log.d("ljh","是我的锅吗?id是" + holder.currentId)
    }

    inner class ViewHolder(private val binding: ItemAllMvBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                onItemClick.invoke(currentId)
            }
        }
        var currentId = 0L
        fun bind(item: DataX){
            Glide.with(binding.root.context).load(item.cover).into(binding.ivItemAllMvCover)
            if (item.briefDesc.isNullOrEmpty()){
                binding.tvItemAllMvDesc.text=item.name
            } else binding.tvItemAllMvDesc.text=item.name+"|"+item.briefDesc
            binding.tvItemMvCounts.visibility=View.GONE
            binding.tvItemMvDetail.visibility= View.GONE
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