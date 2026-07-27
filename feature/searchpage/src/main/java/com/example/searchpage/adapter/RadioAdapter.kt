package com.example.searchpage.adapter

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
import com.example.searchpage.model.RadioEntity

class RadioAdapter(
    private val onItemClick: (RadioEntity) -> Unit
) : ListAdapter<RadioEntity, RadioAdapter.RadioViewHolder>(RadioDiffCallback()) {

    inner class RadioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivRadioCover: ImageView = view.findViewById(R.id.ivRadioCover)
        val tvRadioName: TextView = view.findViewById(R.id.tvRadioName)
        val tvAuthorName: TextView = view.findViewById(R.id.tvAuthorName)

        fun bind(radio: RadioEntity) {
            tvRadioName.text = radio.name
            tvAuthorName.text = radio.dj?.nickname ?: "未知主播"

            Glide.with(itemView.context)
                .load(radio.picUrl)
                .placeholder(android.R.color.darker_gray)
                .into(ivRadioCover)

            itemView.setOnClickListener { onItemClick(radio) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radio, parent, false)
        return RadioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RadioDiffCallback : DiffUtil.ItemCallback<RadioEntity>() {
    override fun areItemsTheSame(oldItem: RadioEntity, newItem: RadioEntity): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: RadioEntity, newItem: RadioEntity): Boolean {
        return oldItem == newItem
    }
}