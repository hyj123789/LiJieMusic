package com.example.comment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.base.BaseFragment
import com.example.comment.databinding.FragmentCommentBinding
import com.example.lijiemusic.core.navigation.RoutePath
import com.therouter.router.Route
import kotlinx.coroutines.launch

class CommentFragment : BaseFragment<FragmentCommentBinding>(FragmentCommentBinding::inflate) {

    private val viewModel: CommentViewModel by viewModels()
    private val commentAdapter = CommentAdapter()

    var id = ""
    var commendId = ""
    var sorttype : Int = 1
    var isLoading = false

    override fun initView() {
        super.initView()
        val songId = arguments?.getString("songId") ?: ""
        id = songId
        val songName = arguments?.getString("songName") ?: "未知歌曲"
        val coverUrl = arguments?.getString("coverUrl") ?: ""
        Log.d("hyj","得到的歌曲数据-${songId},-${songName},-${coverUrl}")
        binding.tvSong.text = songName
        //设置封面
        if (coverUrl.isNotEmpty()) {
            Glide.with(this)
                .load(coverUrl)
                .transform(CircleCrop())
                .into(binding.ivSongCover)
        }
        //获取评论
        if (songId.isNotEmpty()) {
            //去取歌曲的评论
            viewModel.fetchComments(songId,false,sorttype)
        }

        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.adapter = commentAdapter

        //设置点击时间，好去拿楼层数据
        commentAdapter.setOnItemClickListener(object : OnCommentItemClickListener {
            override fun onExpandClick(commendid: Long) {

                //收到 Adapter 的通知，开始处理网络请求
                Toast.makeText(context, "正在请求数据", Toast.LENGTH_SHORT).show()
                //模拟网络请求
                commendId = commendid.toString()
                viewModel.fetchReplies(commendid.toString(),id)
            }
        })
    }

    override fun initEvent() {

        binding.ivBack.setOnClickListener {
            //返回上一个fragment
            findNavController().navigateUp()
        }


        //点击推荐
        binding.tuijian.setOnClickListener {
            Toast.makeText(context, "正在请求推荐数据", Toast.LENGTH_SHORT).show()
            viewModel.fetchComments(id,false,1)
            sorttype = 1
        }
        //点击最热
        binding.zuire.setOnClickListener {
            Toast.makeText(context, "正在请求最热数据", Toast.LENGTH_SHORT).show()
            viewModel.fetchComments(id,false,2)
            sorttype = 2
        }
        //点击最新
        binding.zuixin.setOnClickListener {
            Toast.makeText(context, "正在请求最新数据", Toast.LENGTH_SHORT).show()
            viewModel.fetchComments(id,false,3)
            sorttype = 3
        }


        binding.rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            //触底监听
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //代表在向下滑动
                if (dy > 0){
                    // canScrollVertically(1) 返回 false 说明到底了！
                    if (!recyclerView.canScrollVertically(1)) {
                        //如果当前没有在加载，才去请求新数据
                        if (!isLoading && viewModel.hasMore.value) {
                            //上锁以免多次加载
                            isLoading = true
                            binding.jiazai.visibility = View.VISIBLE
                            binding.jiazai.text = "加载中..."
                            viewModel.fetchComments(id,true,sorttype)
                            Log.d("hyj","开始评论的触底自动刷新,本次id为${id}")
                        }
                    }
                }
            }
        })
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {

                    //获取评论
                    viewModel.comment.collect { list ->
                        if (list.isNotEmpty()) {
                            isLoading = false
                            binding.jiazai.visibility = View.GONE
                            commentAdapter.submitList(list)
                        }
                    }
                }
                launch {
                    //获取评论的总数目
                    viewModel.totalCount.collect { total ->
                        binding.tvTotalComments.text = "评论 ($total)"
                        Log.d("hyj","开始评论设置，本次为总评论${total}")
                    }
                }

                launch {
                    //获取评论题目
                    viewModel.commentsTitle.collect { total ->
                        binding.commentsTitle.text = total
                        Log.d("hyj","开始设置title，本次为${total}")
                    }
                }

                launch {
                    viewModel.replyState.collect { data ->
                        //数据不为空就把数据传给adapter进行插入
                        if (commendId.isNotEmpty()) {
                            if (data.isNotEmpty()) {
                                commentAdapter.insertReplies(commendId.toLong(), data)
                            } else {
                                Toast.makeText(requireContext(), "没有更多回复了", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.hasMore.collect { hasMoreData ->
                        if (hasMoreData) {
                            if (!isLoading) {
                                binding.jiazai.visibility = View.GONE
                            }
                        } else {
                            isLoading = false

                            if (sorttype == 1){
                                binding.jiazai.visibility = View.GONE
                            }else {
                                binding.jiazai.text = "暂无更多评论啦~"
                                binding.jiazai.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

}