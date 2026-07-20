package com.example.comment

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.comment.databinding.ItemCommentBinding

class CommentAdapter : ListAdapter<CommentItem, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    private var mListener: OnCommentItemClickListener? = null

    //设置监听器
    fun setOnItemClickListener(listener: OnCommentItemClickListener) {
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = getItem(position)
        //绑定view
        holder.bind(item)

        if (item.isreply) {
            //如果是子评论,往右缩进48dp，隐藏展开按钮,主评论分开
            holder.setIndent(48)
            holder.binding.tvReply.visibility = View.GONE

        } else {
            //如果是主评论，不缩进，显示展开按钮
            holder.setIndent(0)

            if (item.replyCount > 0) {
                //如果有回复，才显示按钮
                holder.binding.tvReply.visibility = View.VISIBLE
                holder.binding.tvReply.text = if (item.isexpend) {
                    "—— 收起回复 ∧"
                } else {
                    "—— 展开 ${item.replyCount} 条回复 ∨" //
                }
            } else {
                //如果没人回复直接隐藏展开按钮
                holder.binding.tvReply.visibility = View.GONE
            }
        }

        holder.binding.tvReply.setOnClickListener {
            if (item.isexpend) {
                //如果已经展开了直接移除
                Log.d("hyj", "--> 准备收起回复")
                collapseReplies(item.commentId)
            } else {
                //如果还没展开通知 Fragment 去网络请求数据
                //触发接口回调,将数据和 ViewBinding 传出去
                mListener?.onExpandClick(item.commentId)
            }
        }
    }

    private fun collapseReplies(id: Long) {

        val newList = currentList.toMutableList()
        val parentIndex = newList.indexOfFirst { it.commentId == id }

        if (parentIndex != -1) {
            //必须用copy生成新对象，ListAdapter的DiffUtil的第二个判断才能识别到状态改变,不然ui没有变化我靠了
            newList[parentIndex] = newList[parentIndex].copy(isexpend = false)

            //计算它下面跟着多少条连续的子回复
            var removeCount = 0
            for (i in parentIndex + 1 until newList.size) {
                if (newList[i].isreply) {
                    removeCount++
                } else {
                    break
                }
            }
            //连续移除这些子回复
            //索引会自动移动，只对同一个位置移动即可
            for (i in 0 until removeCount) {
                newList.removeAt(parentIndex + 1)
            }
            submitList(newList)
        }
    }

    //彻底重写 insertReplies 方法
    fun insertReplies(parentCommentId: Long, replies: List<CommentItem>) {
        if (replies.isEmpty()) return
        Log.d("hyj", "准备插入回复，拿到的子回复数量: ${replies.size}")

        //获取当前列表并变为可变的列表，方便插入
        val newList = currentList.toMutableList()

        //在新列表里查找主楼层
        val parentIndex = newList.indexOfFirst { it.commentId == parentCommentId }
        Log.d("hyj", "查找主楼层 ID: $parentCommentId，找到的索引是: $parentIndex")

        if (parentIndex == -1) {
            Log.e("hyj", "大无语事件：没找到主楼层，提前 return 了！")
            return
        }

        //赋值他的依赖属性
        val mappedReplies = replies.map {
            it.copy(isreply = true)
        }
        //更新主楼层的数据状态，我靠了，这里也是，我就说为什么文字没有反应
        newList[parentIndex] = newList[parentIndex].copy(isexpend = true)

        //把新拿到的回复列表塞进主楼层的下一行
        newList.addAll(parentIndex + 1, mappedReplies)
        //通知更新列表
        submitList(newList)
    }


    class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentItem) {
            //设置头像
            Glide.with(itemView.context)
                .load(item.user.avatarUrl)
                .transform(CircleCrop())
                .into(binding.ivAvatar)

            //设置昵称
            binding.tvNickname.text = item.user.nickname

            //设置评论内容
            binding.tvContent.text = item.content

            //设置点赞数
            if (item.likedCount > 0) {
                binding.tvLikeCount.text = item.likedCount.toString()
            } else {
                binding.tvLikeCount.text = ""
            }

            //设置时间与地点
            val time = item.timeStr ?: ""
            val location = item.ipLocation?.location ?: ""
            val timeAndLocation = if (location.isNotEmpty()) {
                "$time $location"
            } else time
            binding.tvTimeLocation.text = timeAndLocation

            //点击爱心变红
            binding.ivLike.setOnClickListener {
                item.liked = !item.liked
                if (item.liked){
                    item.likedCount++
                }else item.likedCount--
                updateLikeUI(item)
            }
        }

        fun setIndent(dpValue: Int) {
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            val pxValue = (dpValue * binding.root.context.resources.displayMetrics.density).toInt()
            params.marginStart = pxValue
            binding.root.layoutParams = params
        }

        private fun updateLikeUI(item: CommentItem) {
            //根据是否点赞判断是否是红色
            if (item.liked) {
                binding.ivLike.setImageResource(R.drawable.like)

            } else {
                binding.ivLike.setImageResource(R.drawable.konglike)
            }

            if (item.likedCount > 0) {
                binding.tvLikeCount.text = item.likedCount.toString()
                binding.tvLikeCount.setTextColor(
                    if (item.liked) Color.parseColor("#FF3A3A")
                    else Color.parseColor("#999999"))
            } else {
                binding.tvLikeCount.text = ""
            }
        }
    }

    class CommentDiffCallback : DiffUtil.ItemCallback<CommentItem>() {
        override fun areItemsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
            //利用内ID判断是否是同一个东西
            return oldItem.commentId == newItem.commentId
        }

        override fun areContentsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
            return oldItem == newItem
        }
    }
}

interface OnCommentItemClickListener {
    fun onExpandClick(commendid: Long)

}