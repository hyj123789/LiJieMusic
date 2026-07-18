package com.example.mv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.base.BaseFragment
import com.example.mv.databinding.FragmentMvBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.getValue


class MVFragment :BaseFragment<FragmentMvBinding>(FragmentMvBinding::inflate) {

    private val viewModel : MVViewModel by viewModels()
    private val MvAdapter = VideoAdapter()

    //定义一个全局变量记录当前的Offset
    var currentOffset = 0

    //加上一个锁防止重复请求
    var isLoading = false

    override fun initView() {

        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.rv.adapter = MvAdapter

        viewModel.fetchRecommendPlaylists(currentOffset)

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            //触底监听
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //代表在向下滑动
                if (dy > 0){
                    // canScrollVertically(1) 返回 false 说明到底了！
                    if (!recyclerView.canScrollVertically(1)) {

                        //如果当前没有在加载，才去请求新数据
                        if (!isLoading) {
                            //上锁以免多次加载
                            isLoading = true
                            binding.jiazai.visibility = View.VISIBLE
                            //currentOffset+1保证多次请求
                            viewModel.fetchRecommendPlaylists(currentOffset)
                        }
                    }
                }
            }
        })

    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {

            //只有在页面可见时才监听，不可见就没有必要监听
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.MVFlow
                    .onEach { realData ->
                        if (realData.isNotEmpty()) {
                            //解开限制，允许下一次触底滑动
                            isLoading = false
                            //隐藏底部“正在加载”的文字
                            binding.jiazai.visibility = View.GONE
                            //更新offset
                            currentOffset = currentOffset + 1

                            //把数据交给adapter
                            MvAdapter.submitList(realData)
                        } else {
                            //如果服务器没返回数据说明到底了
                            binding.jiazai.text = "没有更多视频啦~"
                            binding.jiazai.visibility = View.VISIBLE
                            //故意不上解锁这样以后再怎么滑也不会去请求了
                            //isLoading = false
                        }
                    }
                    .launchIn(this)
            }
        }
    }
}