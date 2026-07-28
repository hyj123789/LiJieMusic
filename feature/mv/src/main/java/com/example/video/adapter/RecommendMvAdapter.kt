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

class RecommendMvAdapter :
    ListAdapter<VideoItemWrapper, RecommendMvAdapter.VideoViewHolder>(VideoDiffCallback()) {

    /** 当前正在界面上（已绑定/未回收）的 ViewHolder，用于 releaseAllPlayers 兜底释放 */
    private val activeViewHolders = mutableSetOf<VideoViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mv, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        // 标记为活跃，releaseAllPlayers 兜底释放时会用到
        activeViewHolders.add(holder)

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

        // 增加视频封面图
        val coverImageView = ImageView(holder.itemView.context)
        coverImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(coverImageView)
            .load(videoData.coverUrl)
            .into(coverImageView)

        // 把图片塞给播放器作为封面
        holder.gsyPlayer.thumbImageView = coverImageView

        // 隐藏返回键，列表里的视频一般不需要返回键
        holder.gsyPlayer.backButton.visibility = View.GONE
    }

    /**
     * 回收离屏的ViewHolder时释放GSYVideoPlayer，防止滚动后播放器实例泄漏
     */
    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        activeViewHolders.remove(holder)
        releasePlayer(holder)
    }

    /**
     * RecyclerView被分离时释放所有播放器
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        releaseAllPlayers()
    }

    /**
     * 释放所有GSYVideoPlayer实例，防止视频播放器内存泄漏
     */
    fun releaseAllPlayers() {
        activeViewHolders.toList().forEach { releasePlayer(it) }
        activeViewHolders.clear()
    }

    /**
     * 释放ViewHolder中的GSYVideoPlayer
     */
    fun releasePlayer(holder: VideoViewHolder) {
        holder.gsyPlayer.release()
        holder.gsyPlayer.setVideoAllCallBack(null)
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gsyPlayer: StandardGSYVideoPlayer = itemView.findViewById(R.id.video_player)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_video_title)
        val tvAuthor: TextView = itemView.findViewById(R.id.tv_video_author)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_video_ds)
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<VideoItemWrapper>() {
        override fun areItemsTheSame(
            oldItem: VideoItemWrapper,
            newItem: VideoItemWrapper
        ): Boolean {
            //通过url来判断是不是同一个视频
            return oldItem.data?.urlInfo?.url == newItem.data?.urlInfo?.url
        }

        override fun areContentsTheSame(
            oldItem: VideoItemWrapper,
            newItem: VideoItemWrapper
        ): Boolean {
            return oldItem == newItem
        }
    }
}