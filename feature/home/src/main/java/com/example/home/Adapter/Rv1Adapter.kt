package com.example.home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.home.R
import com.example.home.model.PlaylistInfo1

class Rv1Adapter : RecyclerView.Adapter<Rv1Adapter.ViewHolder>(){

    private val dataList = mutableListOf<PlaylistInfo1>()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.iv1)
        val tv1Title: TextView = itemView.findViewById(R.id.tv1MainTitle)
        val tv1dc : TextView = itemView.findViewById(R.id.tv1SubTitle)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Rv1Adapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Rv1Adapter.ViewHolder, position: Int) {
        val item = dataList[position]
        holder.tv1Title.text = item.name
        holder.tv1dc.text = item.description

        Glide.with(holder.itemView.context)
            .load(item.coverImgUrl)
            .into(holder.ivCover)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(realData: List<PlaylistInfo1>) {
        dataList.clear()
        dataList.addAll(realData)
        notifyDataSetChanged()
    }
}