package com.example.player

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.player.model.LyricLine
import androidx.core.graphics.toColorInt

class LyricAdapter : ListAdapter<LyricLine, LyricAdapter.LyricViewHolder>(LyricDiffCallback()) {

    private var currentLineIndex = -1 //记录当前正在唱的是哪一句

    //重写 submitList，每次换新歌的时候，把高亮进度清零
    override fun submitList(list: List<LyricLine>?) {
        currentLineIndex = -1
        super.submitList(list)
    }

    //核心时间比对逻辑
    fun updateTime(currentMillis: Long): Int {
        //ListAdapter自带currentList
        val list = currentList
        if (list.isEmpty()) return -1

        //找出某一句的时间小于等于当前的时间
        var newIndex = list.indexOfLast { it.timeMillis <= currentMillis }
        if (newIndex == -1) newIndex = 0

        //如果换行了，就局部刷新旧行和新行
        if (newIndex != currentLineIndex) {
            val oldIndex = currentLineIndex
            currentLineIndex = newIndex
            if (oldIndex >= 0) notifyItemChanged(oldIndex)
            notifyItemChanged(currentLineIndex)
            //返回当前行号给外部，让它滚动
            return currentLineIndex
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lyric, parent, false)
        return LyricViewHolder(view)
    }

    override fun onBindViewHolder(holder: LyricViewHolder, position: Int) {
        val line = getItem(position)
        holder.tvLine.text = line.text

        //正在唱的这一句高亮变大变成绿色
        if (position == currentLineIndex) {
            holder.tvLine.textSize = 18f
            holder.tvLine.setTextColor("#A5D6A7".toColorInt())
        } else {
            holder.tvLine.textSize = 16f
            holder.tvLine.setTextColor("#FF000000".toColorInt())
        }
    }

    //用来向外暴露当前是哪一行
    fun getCurrentLineIndex(): Int {
        return currentLineIndex
    }

    class LyricViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLine: TextView = itemView.findViewById(R.id.tv_lyric_line)
    }
}

class LyricDiffCallback : DiffUtil.ItemCallback<LyricLine>() {
    override fun areItemsTheSame(oldItem: LyricLine, newItem: LyricLine): Boolean {
        //把“时间戳”当做唯一标识
        return oldItem.timeMillis == newItem.timeMillis
    }

    override fun areContentsTheSame(oldItem: LyricLine, newItem: LyricLine): Boolean {
        //判断内容有没有改变
        return oldItem.text == newItem.text
    }
}