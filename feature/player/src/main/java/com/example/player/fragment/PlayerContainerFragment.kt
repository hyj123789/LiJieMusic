package com.example.player.fragment

import android.os.Bundle
import android.view.View
import com.example.base.BaseFragment
import com.example.player.adapter.ViewPagerAdapter
import com.example.player.databinding.FragmentPlayerContainerBinding

class PlayerContainerFragment :
    BaseFragment<FragmentPlayerContainerBinding>(FragmentPlayerContainerBinding::inflate) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 关闭 ViewPager2 的状态保存/恢复，防止重建时 Fragment 引用失效崩溃
        binding.mainViewpager2.setSaveEnabled(false)
        val list = listOf({ PlayerFragment() }, { WikiFragment() })
        binding.mainViewpager2.adapter = ViewPagerAdapter(this, list)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}
