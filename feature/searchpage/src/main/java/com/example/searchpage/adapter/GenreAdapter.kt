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
import com.example.searchpage.model.GenreSubTag

class GenreAdapter(
    private val onItemClick: (GenreSubTag) -> Unit
) : ListAdapter<GenreSubTag, GenreAdapter.GenreViewHolder>(GenreDiffCallback()) {

    inner class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivGenreBg: ImageView = view.findViewById(R.id.ivGenreBg)
        val tvGenreName: TextView = view.findViewById(R.id.tvGenreName)

        fun bind(genre: GenreSubTag) {
            tvGenreName.text = genre.tagName

            val rawUrl = genre.picUrl ?: ""

            val cleanPath = rawUrl.replace("yyimgs/", "")

            val realUrl = "https://p1.music.126.net/$cleanPath"
            android.util.Log.d("ImageDebug", "最终拼接的图片地址是: $realUrl")

            Glide.with(ivGenreBg)
                .load(realUrl)
                .error(android.R.color.holo_red_dark)
                .into(ivGenreBg)

            itemView.setOnClickListener { onItemClick(genre) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_genre, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class GenreDiffCallback : DiffUtil.ItemCallback<GenreSubTag>() {
    override fun areItemsTheSame(oldItem: GenreSubTag, newItem: GenreSubTag) = oldItem.tagId == newItem.tagId
    override fun areContentsTheSame(oldItem: GenreSubTag, newItem: GenreSubTag) = oldItem == newItem
}