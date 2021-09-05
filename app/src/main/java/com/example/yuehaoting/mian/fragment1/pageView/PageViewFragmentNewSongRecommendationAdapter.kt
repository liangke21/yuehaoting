package com.example.yuehaoting.mian.fragment1.pageView

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.yuehaoting.mian.fragment1.newSongRecommendationFragment.BaseFragmentNewSongRecommendation

/**
 * 作者: LiangKe
 * 时间: 2021/9/4 18:17
 * 描述:
 */
class PageViewFragmentNewSongRecommendationAdapter (fm: FragmentManager, private var fragmentList:ArrayList<BaseFragmentNewSongRecommendation>): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)  {

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}