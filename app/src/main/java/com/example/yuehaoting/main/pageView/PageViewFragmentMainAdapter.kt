package com.example.yuehaoting.main.pageView

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.yuehaoting.base.fragmet.BaseFragment

/**
 * 作者: 天使
 * 时间: 2021/8/28 16:05
 * 描述:
 */
class PageViewFragmentMainAdapter(fm: FragmentManager, private var fragmentList:ArrayList<Fragment>): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {



    override fun getCount(): Int {
       return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }


}