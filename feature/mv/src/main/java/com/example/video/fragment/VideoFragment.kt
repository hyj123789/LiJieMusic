package com.example.video.fragment

import com.example.base.BaseFragment
import com.example.video.adapter.ViewPagerAdapter
import com.example.video.databinding.FragmentVideoBinding
import com.google.android.material.tabs.TabLayoutMediator

class VideoFragment : BaseFragment<FragmentVideoBinding>(FragmentVideoBinding::inflate) {
    private val list= listOf(
        { AllMvFragment()},{ TopMvFragment()},{ MvRecommendFragment()}
    )
    override fun initView() {
        super.initView()
        binding.apply {
            viewPager.adapter = ViewPagerAdapter(requireActivity(), list)
            viewPager.offscreenPageLimit = 3
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