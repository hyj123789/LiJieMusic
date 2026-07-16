package com.example.home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.home.R
import com.example.home.model.SongItem

class Rv3Adapte : RecyclerView.Adapter<Rv3Adapte.ViewHolder>(){

    private val dataList = mutableListOf<SongItem>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img3: ImageView = itemView.findViewById(R.id.img3)
        val tv3singer: TextView = itemView.findViewById(R.id.singer)
        val tv3sing : TextView = itemView.findViewById(R.id.sing)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Rv3Adapte.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item3, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Rv3Adapte.ViewHolder, position: Int) {
        val item = dataList[position]

        //换上最爱的歌单名
        holder.tv3sing.text = item.ar.firstOrNull()?.name ?: "未知歌手"
        holder.tv3singer.text  = item.name

        Glide.with(holder.itemView.context)
            .load(item.al.picUrl)
            .into(holder.img3)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(newList: List<SongItem>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }

}