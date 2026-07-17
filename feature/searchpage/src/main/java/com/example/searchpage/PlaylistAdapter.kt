package com.example.searchpage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaylistAdapter(
    private val dataList: MutableList<PlaylistItem> = mutableListOf()
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.img)
        val tvMainTitle: TextView = itemView.findViewById(R.id.tv_main_title)
        val tvSubTitle: TextView = itemView.findViewById(R.id.tv_sub_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_playlist,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = dataList[position]

        //取第一个tag作为主标题
        val mainTag = playlist.tags?.firstOrNull() ?: "精选推荐"
        holder.tvMainTitle.text = mainTag

        //副标题
        holder.tvSubTitle.text = playlist.name

        Glide.with(holder.itemView.context)
            .load(playlist.coverImgUrl)
            .into(holder.ivCover)

    }

    fun setData(newList: List<PlaylistItem>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }

}