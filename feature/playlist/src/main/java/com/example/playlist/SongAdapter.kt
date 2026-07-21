package com.example.playlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.example.playlist.databinding.ItemSongBinding
import com.example.playlist.model.Track

class SongAdapter : ListAdapter<Track, SongAdapter.ViewHolder>(SongDiffCallback()){

    private var listener: SongAdapter.OnSongClickListener? = null

    //暴露一个给外部调用的设置方法
    fun OnSongClickListener(listener: OnSongClickListener) {
        this.listener = listener
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val song = getItem(position)
        holder.bind(song)

        val artistName = song.artists?.joinToString(separator = "|") { it.name } ?: "未知歌手"

        holder.binding.ivCover.setOnClickListener {
            listener?.onSongNextPlayClick(song.id.toString(), song.name, artistName)
        }

        holder.binding.btnPlay.setOnClickListener {
            listener?.onSongPlayClick(song.id.toString(), song.name, artistName)
        }
    }
    fun moveItem(from : Int,to : Int){
        val list = currentList.toMutableList()
        val track = list.removeAt(from)
        list.add(to,track)
        submitList(list)
    }
    fun removeItem(position: Int){
        val list = currentList.toMutableList()
        list.removeAt(position)
        submitList(list)
    }
    class ViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Track){
            binding.apply {
                Glide.with(binding.root.context).load(item.album?.picUrl).into(ivCover)
                tvSongName.text=item.name
                tvArtistName.text=item.artists?.joinToString(separator = "|") { it.name }
                when(item.fee){
                    0 -> ivVip.visibility = View.GONE
                    1 -> {
                        ivVip.visibility = View.VISIBLE
                        ivVip.setImageResource(R.drawable.ic_vip)
                    }
                    8 -> {
                        ivVip.visibility = View.VISIBLE
                        ivVip.setImageResource(R.drawable.ic_fee)
                        val paddingPx = (5 * binding.root.context.resources.displayMetrics.density + 0.5f).toInt()
                        ivVip.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    }
                }
            }
        }
    }

    interface OnSongClickListener {
        fun onSongPlayClick(id: String, songName: String, artistName: String)
        fun onSongNextPlayClick(id: String, songName: String, artistName: String)
    }
}

class SongDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(
        oldItem: Track,
        newItem: Track
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Track,
        newItem: Track
    ): Boolean {
        return oldItem == newItem
    }
}