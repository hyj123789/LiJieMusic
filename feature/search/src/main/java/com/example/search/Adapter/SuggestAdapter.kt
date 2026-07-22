package com.example.search.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.search.R
import com.example.search.model.SuggestSong

class SuggestAdapter(
    private val onItemClick: (String) -> Unit
)  : ListAdapter<String, SuggestAdapter.SuggestViewHolder>(SuggestDiffCallback()){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestAdapter.SuggestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_suggest, parent, false)
        return SuggestViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestAdapter.SuggestViewHolder, position: Int) {
        val itemText = getItem(position)
        holder.tvSuggestText.text = itemText
    }

    inner class SuggestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSuggestText: TextView = itemView.findViewById(R.id.tv_search_suggest)

        init {
            //设置点击事件
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }
    }

    class SuggestDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}