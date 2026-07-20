package com.example.dynamics

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.base.BaseActivity
import com.example.dynamics.databinding.ActivityDynamicsBinding
import com.example.model.UserManager
import com.example.therouter.RoutePath
import com.example.util.ToastUtil
import com.therouter.router.Route
import kotlinx.coroutines.launch
@Route(path = RoutePath.DYNAMICS_MAIN)
class DynamicsActivity : BaseActivity<ActivityDynamicsBinding>(ActivityDynamicsBinding::inflate) {
    private val viewModel : DynamicsViewModel by viewModels()
    private val mAdapter = DynamicsAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            lifecycleScope.launch{
                UserManager.profile.collect {profile ->
                    profile?.apply {
                        tvNickname.text = profile.nickname
                        tvVip.text = if (profile.vipType == 0) "非会员" else "会员"
                        Glide.with(this@DynamicsActivity).load(profile.avatarUrl).circleCrop().into(ivAvatar)
                        tvSignature.text = if(profile.signature.isNullOrBlank()) "这个人很懒，什么也没有留下" else profile.signature
                        tvFollows.text = "${profile.follows}关注"
                        tvFolloweds.text = "${profile.followeds}粉丝"
                        Log.d("ljh","个人信息"+profile.toString())
                    }
                }
            }
            rvDynamics.apply {
                adapter=mAdapter
                layoutManager= LinearLayoutManager(this@DynamicsActivity)
            }
        }
        viewModel.init()
    }

    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.rvList.collect { list->
                        list?.apply {
                            val newList = list.toMutableList()
                            mAdapter.submitList(newList)
                        }
                    }
                }
                launch {
                    viewModel.toastMsg.collect { msg->
                        msg?.let {
                            ToastUtil.popToast(msg,this@DynamicsActivity)
                        }
                    }
                }
            }
        }
    }
}