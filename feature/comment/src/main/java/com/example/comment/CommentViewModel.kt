package com.example.comment

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import com.example.base.BaseViewModel
import com.example.net.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel : BaseViewModel() {

    private val _comment = MutableStateFlow<List<CommentItem>>(emptyList())
    val comment : StateFlow<List<CommentItem>> get() = _comment
    private var _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount

    private var _commentsTitle = MutableStateFlow("还看呢，加载不出来了，好绕")
    val commentsTitle: StateFlow<String> = _commentsTitle

    private var _replyState = MutableStateFlow<List<CommentItem>>(emptyList())
    val replyState: StateFlow<List<CommentItem>> = _replyState

    //通知fragment是否还有更多数据
    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore


    //private var currentOffset = 0
    //private val pageSize = 20

    private var currentCursor: String? = null //用于时间排序的游标
    private var currentPageNo = 1             //用于推荐和热度排序的页码
    private val pageSize = 10                //请求的数目

    //传id查评论
    fun fetchComments(songId: String, loadmore: Boolean,sortType: Int) {
        viewModelScope.launch {
            try {
                //没有数据直接返回
                if (loadmore && !_hasMore.value) {
                    return@launch
                }

                //如果是刷新或第一次进页面就重置所有分页状态
                if (!loadmore) {
                    currentCursor = null
                    currentPageNo = 1
                    _hasMore.value = true
                }

                val api = RetrofitClient.createApi(CommentApi::class.java)

                //调用新接口，传入相应的游标和页码
                val response = api.getNewComments(
                    id = songId,
                    type = 0,
                    sortType = sortType,
                    pageNo = currentPageNo,
                    pageSize = pageSize,
                    cursor = currentCursor
                )



                // 提取列表数据
                val newComments = response.data?.comments ?: emptyList()

                if (loadmore) {
                    //如果是加载更多把新列表拼在旧数据后面
                    val oldList = _comment.value
                    _comment.value = oldList + newComments
                } else {
                    //如果是第一次加载直接赋值
                    _comment.value = newComments
                }

                // 更新游标（服务器不返回时保持 null）
                currentCursor = response.data?.cursor
                // 更新页码，为下一次拉取做准备
                currentPageNo++
                // 更新触底状态，直接沿用服务器返回的 hasMore 布尔值
                _hasMore.value = response.data?.hasMore ?: false

                //更新评论数目
                _totalCount.value = response.data?.totalCount ?: 0
                //更新评论标题
                _commentsTitle.value = response.data?.commentsTitle?:"不可能为空啊，不是为什么非要让我处理"


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchReplies(commentId: String,songId: String) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.createApi(CommentApi::class.java)
                val response = api.getFloorComments(commentId.toLong(),songId,0,10,null)
                Log.d("hyj","楼层评论返回码：${response.code},返回的评论数目为${response.data.comments?.size}")
                _replyState.value = response?.data?.comments?:emptyList()
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
}