package com.example.video.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.video.databinding.ItemAllMvBinding
import com.example.video.model.DataX

class AllMvAdapter : ListAdapter<DataX, AllMvAdapter.ViewHolder>(DataXDiffCallback()) {
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
    }

    inner class ViewHolder(private val binding: ItemAllMvBinding) : RecyclerView.ViewHolder(binding.root){
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