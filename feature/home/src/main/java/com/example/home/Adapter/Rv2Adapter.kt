package com.example.home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.home.R
import com.example.home.model.PlaylistInfo2

class Rv2Adapter : RecyclerView.Adapter<Rv2Adapter.ViewHolder>() {


    //存放歌单数据的列表
    private val dataList = mutableListOf<PlaylistInfo2>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Rv2Adapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Rv2Adapter.ViewHolder, position: Int) {
        val item = dataList[position]

        //换上最爱的歌单名
        holder.tv2Title.text = item.name

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.img2)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(realData: List<PlaylistInfo2>) {
        dataList.clear()
        dataList.addAll(realData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img2: ImageView = itemView.findViewById(R.id.iv2)
        val tv2Title: TextView = itemView.findViewById(R.id.tv2MainTitle)
    }

}