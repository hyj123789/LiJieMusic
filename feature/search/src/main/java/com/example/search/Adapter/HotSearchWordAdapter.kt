package com.example.search.Adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.search.R
import com.example.search.model.HotSearchData

class HotSearchWordAdapter : ListAdapter<HotSearchData, HotSearchWordAdapter.WordViewHolder>(HotSearchDiffCallback()) {

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank: TextView = itemView.findViewById(R.id.tv_rank)
        val tv: TextView = itemView.findViewById(R.id.tv)
        val imgHot: ImageView = itemView.findViewById(R.id.img_hot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hot_search, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val item = getItem(position)

        val rank = position + 1
        holder.tvRank.text = rank.toString()

        if (rank <= 3) {
            holder.tvRank.setTextColor(Color.parseColor("#FF3A3A")) //红色
            holder.tv.typeface = Typeface.create(holder.tv.typeface, Typeface.BOLD)
        } else {
            holder.tvRank.setTextColor(Color.parseColor("#999999")) //灰色
            holder.tv.typeface = Typeface.create(holder.tv.typeface, Typeface.NORMAL)
        }

        holder.tv.text = item.searchWord

        if (!item.iconUrl.isNullOrEmpty()) {
            holder.imgHot.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(item.iconUrl)
                .into(holder.imgHot)
        } else {
            holder.imgHot.visibility = View.GONE
        }
    }

    class HotSearchDiffCallback : DiffUtil.ItemCallback<HotSearchData>() {

        override fun areItemsTheSame(oldItem: HotSearchData, newItem: HotSearchData): Boolean {
            return oldItem.searchWord == newItem.searchWord
        }

        override fun areContentsTheSame(oldItem: HotSearchData, newItem: HotSearchData): Boolean {
            return oldItem == newItem
        }
    }
}