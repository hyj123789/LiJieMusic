package com.example.video.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.video.R
import com.example.video.model.VideoItemWrapper
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

class RecommendMvAdapter : ListAdapter<VideoItemWrapper, RecommendMvAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mv, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        //拿到具体的数据对象
        val videoData = getItem(position).data ?: return

        //设置文本信息
        holder.tvTitle.text = videoData.title ?: "未知标题"
        holder.tvAuthor.text = videoData.creator?.nickname ?: "未知作者"
        holder.tvDesc.text = videoData.description ?: ""

        //设置 GSYVideoPlayer 核心播放逻辑
        val videoUrl = videoData.urlInfo?.url ?: ""
        // setUp 方法参数：播放地址，是否缓存，视频标题
        holder.gsyPlayer.setUp(videoUrl, true, videoData.title)

        // 增加视频封面图 (这是 GSYVideoPlayer 非常贴心的功能)
        val coverImageView = ImageView(holder.itemView.context)
        coverImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(holder.itemView.context)
            .load(videoData.coverUrl)
            .into(coverImageView)

        // 把图片塞给播放器作为封面
        holder.gsyPlayer.thumbImageView = coverImageView

        // 隐藏返回键，列表里的视频一般不需要返回键
        holder.gsyPlayer.backButton.visibility = View.GONE
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gsyPlayer: StandardGSYVideoPlayer = itemView.findViewById(R.id.video_player)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_video_title)
        val tvAuthor: TextView = itemView.findViewById(R.id.tv_video_author)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_video_ds)
    }

    // 差异比对器
    class VideoDiffCallback : DiffUtil.ItemCallback<VideoItemWrapper>() {
        override fun areItemsTheSame(oldItem: VideoItemWrapper, newItem: VideoItemWrapper): Boolean {
            //通过url来判断是不是同一个视频
            return oldItem.data?.urlInfo?.url == newItem.data?.urlInfo?.url
        }

        override fun areContentsTheSame(oldItem: VideoItemWrapper, newItem: VideoItemWrapper): Boolean {
            return oldItem == newItem
        }
    }
}