package com.example.profile

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.base.BaseFragment
import com.example.model.UserManager
import com.example.profile.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate){
    private val viewModel: ProfileViewModel by viewModels()
    private val adapter = PlaylistAdapter()
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
            adapter=adapter
            layoutManager= LinearLayoutManager(requireContext())
        }
    }

    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.listData.collect {list->
                        list?.apply {
                            adapter.submitList(list)
                        } ?: return@collect
                    }
                }
            }
        }
    }
}