package com.example.yuehaoting.searchfor.pagerview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.yuehaoting.searchfor.fragment.BaseFragment

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 15:41
 * 描述:
 */
class MyPagerAdapter(fm: FragmentManager, private var fragmentList:List<BaseFragment>): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {



    override fun getCount(): Int {
      return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}