package com.example.search.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.search.R
import com.example.search.model.HotSearchItem

class GuessAdapter : ListAdapter<HotSearchItem, GuessAdapter.GuessViewHolder>(GuessDiffCallback()) {

    class GuessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvKeyword: TextView = itemView.findViewById(R.id.tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guess, parent, false)
        return GuessViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuessViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvKeyword.text = item.first
    }

    class GuessDiffCallback : DiffUtil.ItemCallback<HotSearchItem>() {

        override fun areItemsTheSame(oldItem: HotSearchItem, newItem: HotSearchItem): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(oldItem: HotSearchItem, newItem: HotSearchItem): Boolean {
            return oldItem == newItem
        }
    }
}