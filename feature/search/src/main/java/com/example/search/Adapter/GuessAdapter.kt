package com.example.search.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.search.R
import com.example.search.model.HotSearchItem

class GuessAdapter : RecyclerView.Adapter<GuessAdapter.GuessViewHolder>() {

    private val dataList = mutableListOf<HotSearchItem>()

    fun setData(list: List<HotSearchItem>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guess, parent, false)
        return GuessViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuessViewHolder, position: Int) {
        val item = dataList[position]

        holder.tvKeyword.text = item.first
    }

    override fun getItemCount(): Int = dataList.size

    class GuessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvKeyword: TextView = itemView.findViewById(R.id.tv)
    }
}