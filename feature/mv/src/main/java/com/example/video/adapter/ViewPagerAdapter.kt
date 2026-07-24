package com.example.video.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragmentCreators: List<() -> Fragment>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    constructor(
        fragment: Fragment,
        fragmentCreators: List<() -> Fragment>
    ) : this(fragment.childFragmentManager, fragment.lifecycle, fragmentCreators)
    constructor(
        activity: FragmentActivity,
        fragmentCreators: List<() -> Fragment>
    ) : this(activity.supportFragmentManager, activity.lifecycle, fragmentCreators)

    override fun getItemCount(): Int = fragmentCreators.size

    override fun createFragment(position: Int): Fragment {
        return fragmentCreators[position].invoke()
    }
}