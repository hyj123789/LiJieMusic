package com.example.video.fragment

import androidx.fragment.app.Fragment
import com.example.base.BaseFragment
import com.example.video.adapter.ViewPagerAdapter
import com.example.video.databinding.FragmentVideoBinding
import com.google.android.material.tabs.TabLayoutMediator

class VideoFragment : BaseFragment<FragmentVideoBinding>(FragmentVideoBinding::inflate) {
    private val list by lazy { listOf<Fragment>(AllMvFragment(), TopMvFragment(), MvRecommendFragment()) }
    private val mAdapter by lazy { ViewPagerAdapter(this, list) }
    override fun initView() {
        super.initView()
        binding.apply {
            viewPager.adapter = mAdapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "全部"
                    1 -> tab.text = "热门"
                    2 -> tab.text = "推荐"
                }
            }.attach()
        }
    }
}