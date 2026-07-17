package com.example.search.Adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.search.R
import com.example.search.model.HotSearchData

class HotSearchWordAdapter : RecyclerView.Adapter<HotSearchWordAdapter.WordViewHolder>() {

    private val dataList = mutableListOf<HotSearchData>()

    fun setData(list: List<HotSearchData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hot_search, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val item = dataList[position]

        //设置排名数字
        val rank = position + 1
        holder.tvRank.text = rank.toString()

        //前三名标红色，后面的标浅灰色
        if (rank <= 3) {
            holder.tvRank.setTextColor(Color.parseColor("#FF3A3A")) //红色
            holder.tv.typeface = Typeface.create(holder.tv.typeface, Typeface.BOLD)
        } else {
            holder.tvRank.setTextColor(Color.parseColor("#999999")) //灰色
            holder.tv.typeface = Typeface.create(holder.tv.typeface, Typeface.NORMAL)
        }

        //设置搜索词
        holder.tv.text = item.searchWord

        //"爆"小图标
        if (!item.iconUrl.isNullOrEmpty()) {
            holder.imgHot.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(item.iconUrl)
                .into(holder.imgHot)
        } else {
            //如果接口没给图片就不可见
            holder.imgHot.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = dataList.size

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank: TextView = itemView.findViewById(R.id.tv_rank)
        val tv: TextView = itemView.findViewById(R.id.tv)
        val imgHot: ImageView = itemView.findViewById(R.id.img_hot)
    }
}