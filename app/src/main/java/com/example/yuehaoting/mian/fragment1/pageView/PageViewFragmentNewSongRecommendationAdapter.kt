package com.example.yuehaoting.mian.fragment1.pageView

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.yuehaoting.mian.fragment1.newSongRecommendationFragment.BaseFragmentNewSongRecommendation

/**
 * 作者: LiangKe
 * 时间: 2021/9/4 18:17
 * 描述:
 */
class PageViewFragmentNewSongRecommendationAdapter (fm: FragmentManager, private var fragmentList:ArrayList<BaseFragmentNewSongRecommendation>,lifecycle: Lifecycle): FragmentStateAdapter(
    fm, lifecycle )  {

/*
    override fun getCount(): Int {

    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
*/

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}