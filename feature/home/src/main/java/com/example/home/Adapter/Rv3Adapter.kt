package com.example.home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.home.R
import com.example.home.model.SongItem

class Rv3Adapte : ListAdapter<SongItem, Rv3Adapte.ViewHolder>(SongDiffCallback()) {

    //声明一个私有的、可空的接口变量
    private var listener: OnSongClickListener? = null

    //暴露一个给外部调用的设置方法
    fun OnSongClickListener3(listener: OnSongClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img3: ImageView = itemView.findViewById(R.id.img3)
        val tv3singer: TextView = itemView.findViewById(R.id.singer)
        val tv3sing : TextView = itemView.findViewById(R.id.sing)

        val img3Play : ImageView = itemView.findViewById(R.id.img3_play)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item3, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tv3sing.text = item.ar.firstOrNull()?.name ?: "未知歌手"
        holder.tv3singer.text  = item.name

        Glide.with(holder.itemView.context)
            .load(item.al.picUrl)
            .into(holder.img3)

        holder.img3.setOnClickListener {
            listener?.onSongNextPlayClick(item.id.toString(), item.name,  item.ar.firstOrNull()?.name ?: "未知歌手")
        }

        holder.img3Play.setOnClickListener {
            listener?.onSongPlayClick(item.id.toString(), item.name,  item.ar.firstOrNull()?.name ?: "未知歌手")
        }


    }

    class SongDiffCallback : DiffUtil.ItemCallback<SongItem>() {

        override fun areItemsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
            return oldItem == newItem
        }
    }
    interface OnSongClickListener {
        fun onSongPlayClick(id: String, songName: String, artistName: String)
        fun onSongNextPlayClick(id: String, songName: String, artistName: String)
    }
}