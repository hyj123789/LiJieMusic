package com.example.dynamics

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dynamics.databinding.ItemDynamicBinding
import com.example.dynamics.model.Event
import kotlinx.datetime.Instant
import kotlinx.datetime.*

class DynamicsAdapter : ListAdapter<Event, DynamicsAdapter.ViewHolder> (DynamicsCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDynamicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemDynamicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Event) {
            binding.apply {
                try {
                    Glide.with(binding.root.context).load(item.user.avatarUrl).into(ivDynamicAvatar)
                    tvDynamicsName.text = item.user.nickname
                    tvDynamicsTime.text = formatTimestamp(item.eventTime)
                    tvDynamicsText.text = item.json.msg
                    if (item.pics.isNotEmpty()) {
                        ivDynamicPhoto.visibility = android.view.View.VISIBLE
                        Glide.with(binding.root.context).load(item.pics[0].originUrl).into(ivDynamicPhoto)
                    } else {
                        ivDynamicPhoto.visibility = android.view.View.GONE
                    }
                    Glide.with(binding.root.context).load(item.json.song.album.picUrl).into(ivDynamicSongAvatar)
                    tvDynamicsSong.text = item.json.song.name
                    tvDynamicsArtist.text = item.json.song.artists.joinToString(separator = "|"){ it.name }
                } catch (e: Exception) {
                    Log.d("ljh","动态绑定数据的时候出现异常"+e.message)
                }
            }
        }
    }
    fun formatTimestamp(timestamp: Long): String {
        // 将毫秒时间戳转为 Instant
        val instant = Instant.fromEpochMilliseconds(timestamp)

        // 指定时区（以中国标准时间为例）
        val zone = TimeZone.of("Asia/Shanghai")

        // 转为本地时间并格式化
        val localDateTime = instant.toLocalDateTime(zone)
        return "${localDateTime.year}-${localDateTime.monthNumber}-${localDateTime.dayOfMonth} " +
                "${localDateTime.hour}:${localDateTime.minute}:${localDateTime.second}"
    }

    class DynamicsCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(
            oldItem: Event,
            newItem: Event
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Event,
            newItem: Event
        ): Boolean {
            return oldItem == newItem
        }
    }
}