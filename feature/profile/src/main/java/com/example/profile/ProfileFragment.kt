package com.example.profile

import android.net.Uri
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.base.BaseFragment
import com.example.model.UserManager
import com.example.profile.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate){
    private val viewModel: ProfileViewModel by viewModels()
    private val mAdapter = PlaylistAdapter({id->
        findNavController().navigate(Uri.parse("musicapp://playlist/$id"))
        Log.d("ljh","DeepLink传歌单ID跳转"+id)
    })
    override fun initView() {
        super.initView()
        lifecycleScope.launch{
            UserManager.profile.collect { profile ->
                if (profile==null){
                    Log.d("ljh","我超伟，profile信息为空")
                    return@collect
                } else {
                    binding.apply {
                        tvNickname.text = profile.nickname
                        tvVip.text = if (profile.vipType == 0) "非会员" else "会员"
                        Glide.with(requireContext()).load(profile.avatarUrl).circleCrop().into(ivAvatar)
                        tvSignature.text = if(profile.signature.isNullOrBlank()) "这个人很懒，什么也没有留下" else profile.signature
                        tvFollows.text = "${profile.follows}关注"
                        tvFolloweds.text = "${profile.followeds}粉丝"
                        Log.d("ljh","个人信息"+profile.toString())
                    }
                }
            }
        }
        binding.rvPlaylist.apply {
            adapter=mAdapter
            layoutManager= LinearLayoutManager(requireContext())
            Log.d("ljh","RV初始化成功")
        }
        viewModel.loadPlaylist()
    }

    override fun initEvent() {
        super.initEvent()
        binding.tvCheckAll.setOnClickListener {
            mAdapter.modifyLimited()
            if (mAdapter.getLimited()) {
                binding.tvCheckAll.text = "查看全部"
            } else {
                binding.tvCheckAll.text = "查看部分"
            }
            mAdapter.notifyDataSetChanged()
        }
    }
    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.listData.collect {oleList->
                        oleList?.apply {
                            val newList = this.toMutableList()
                            mAdapter.submitList(newList)
                            binding.tvPlaylistCount.text="${newList.size}个歌单"
                            Log.d("ljh","塔台呼叫，这边是observer，已经观察到了list变化了，adapter已经提交"+newList.toString())
                        } ?: return@collect
                    }
                }
            }
        }
    }
}