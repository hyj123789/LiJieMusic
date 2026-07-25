package com.example.video.fragment

import androidx.viewpager2.widget.ViewPager2
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
            // 关闭 ViewPager2 的状态保存/恢复，防止 Navigation 切 Tab 重建时崩溃
            viewPager.setSaveEnabled(false)
            viewPager.adapter = ViewPagerAdapter(this@VideoFragment, list)
            viewPager.offscreenPageLimit = 1
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "全部"
                    1 -> tab.text = "热门"
                    2 -> tab.text = "推荐"
                }
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
