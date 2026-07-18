package com.example.lijiemusic

import androidx.navigation.fragment.NavHostFragment
import com.example.base.BaseActivity
import com.example.lijiemusic.databinding.ActivityLaunchBinding

class LaunchActivity : BaseActivity<ActivityLaunchBinding>(ActivityLaunchBinding::inflate) {

    override fun initView() {
        // 获取 NavController，后续用于控制页面跳转
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}