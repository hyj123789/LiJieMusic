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
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.video.R
import com.example.video.model.HotComment

//MV评论的RvAdpter
class MvCommentAdapter : ListAdapter<HotComment, MvCommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mvcomment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val tvNickname: TextView = itemView.findViewById(R.id.tv_mvnickname)
        private val tvTimeLocation: TextView = itemView.findViewById(R.id.tv_time_location)
        private val ivLike: ImageView = itemView.findViewById(R.id.iv_mvlike)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_mvcontent)

        fun bind(comment: HotComment) {
            val user = comment.user
            tvNickname.text = user?.nickname ?: "未知用户"
            Glide.with(itemView.context)
                .load(user?.avatarUrl)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(ivAvatar)
            tvContent.text = comment.content ?: ""
            tvTimeLocation.text = comment.timeStr ?: ""

            tvLikeCount.text = comment.likedCount.toString()

            if (comment.liked) {
                ivLike.setImageResource(R.drawable.ic_like)
            } else {
                ivLike.setImageResource(R.drawable.ic_kongxin)
            }

            ivLike.setOnClickListener {
                comment.liked = !comment.liked
                if (comment.liked) {
                    ivLike.setImageResource(R.drawable.ic_like)
                } else {
                    ivLike.setImageResource(R.drawable.ic_kongxin)
                }
            }
        }
    }
}


class CommentDiffCallback : DiffUtil.ItemCallback<HotComment>() {
    override fun areItemsTheSame(oldItem: HotComment, newItem: HotComment): Boolean {
        return oldItem.commentId == newItem.commentId
    }
    override fun areContentsTheSame(oldItem: HotComment, newItem: HotComment): Boolean {
        return oldItem == newItem
    }
}