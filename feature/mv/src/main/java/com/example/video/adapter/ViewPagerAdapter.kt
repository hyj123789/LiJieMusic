package com.example.video.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragment: Fragment,
    private val fragmentList: List<Fragment>
) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment = fragmentList[position]
    override fun getItemCount(): Int = fragmentList.size
}