package com.example.searchpage.adapter

import android.annotation.SuppressLint
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
import com.example.searchpage.model.TopListEntity

class TopListAdapter(
    private val onItemClick: (TopListEntity) -> Unit
) : ListAdapter<TopListEntity, TopListAdapter.TopListViewHolder>(TopListDiffCallback()) {

    inner class TopListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvUpdateFrequency: TextView = view.findViewById(R.id.tvUpdateFrequency)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)

        fun bind(item: TopListEntity) {
            tvName.text = item.name
            tvUpdateFrequency.text = item.updateFrequency
            tvDescription.text = item.description ?: "暂无榜单描述"

            Glide.with(itemView.context)
                .load(item.coverImgUrl)
                .into(ivCover)

            // 设置整个卡片的点击事件
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_content, parent, false)
        return TopListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TopListDiffCallback : DiffUtil.ItemCallback<TopListEntity>() {
    override fun areItemsTheSame(oldItem: TopListEntity, newItem: TopListEntity): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: TopListEntity, newItem: TopListEntity): Boolean {
        return oldItem == newItem
    }
}