package com.example.player.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.base.SongDetail
import com.example.player.R

class PlaylistAdapter(
    private val onItemClick: (SongDetail) -> Unit,
    private val onDeleteClick: (SongDetail) -> Unit
) : ListAdapter<SongDetail, PlaylistAdapter.ViewHolder>(DiffCallback()) {

    private var currentPlayingId: Long = -1

    fun updateCurrentPlaying(songId: Long) {
        currentPlayingId = songId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_palylist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvSongName: TextView = itemView.findViewById(R.id.songs)
        private val tvSinger: TextView = itemView.findViewById(R.id.songer)
        private val ivDelete: ImageView = itemView.findViewById(R.id.imagdelete)

        fun bind(song: SongDetail) {
            tvSongName.text = song.name
            tvSinger.text = " - ${song.ar.get(0).name}"
            Log.d("hyj","绑定的数据${song.name},${song.ar.get(0).name}")

            if (song.id == currentPlayingId) {
                //当前播放标绿
                tvSongName.setTextColor("#A5D6A7".toColorInt())
            } else {
                //其他播放标黑
                tvSongName.setTextColor(Color.BLACK)
            }

            //点击整行触发切歌
            itemView.setOnClickListener {
                onItemClick(song)
            }

            //点击删除按钮触发删除
            ivDelete.setOnClickListener {
                onDeleteClick(song)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SongDetail>() {
        override fun areItemsTheSame(oldItem: SongDetail, newItem: SongDetail): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SongDetail, newItem: SongDetail): Boolean = oldItem == newItem
    }
}