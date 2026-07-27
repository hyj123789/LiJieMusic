package com.example.searchpage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt
import com.example.searchpage.R

class WheelCategoryAdapter : ListAdapter<String, WheelCategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    //记录当前的索引
    var selectedPosition = 0

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wheel_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.tvCategoryName.text = category

        if (position == selectedPosition) {
            holder.tvCategoryName.setTextColor("#333333".toColorInt())
            holder.tvCategoryName.paint.isFakeBoldText = true
        } else {
            holder.tvCategoryName.setTextColor("#999999".toColorInt())
            holder.tvCategoryName.paint.isFakeBoldText = false
        }
    }

    fun updateSelectedPosition(newPosition: Int) {
        if (selectedPosition != newPosition) {
            val oldPos = selectedPosition
            selectedPosition = newPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(newPosition)
        }
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}