package com.example.yuehaoting.searchFor.pagerview

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.yuehaoting.base.fragmet.LazyBaseFragment

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 15:41
 * 描述:
 */
class MyPagerAdapter( fm: FragmentManager, private var fragmentList:ArrayList<LazyBaseFragment>): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {



    override fun getCount(): Int {
      return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

fun clear(fragmentList:ArrayList<LazyBaseFragment>){
    fragmentList.removeAll(fragmentList)
}

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)


    }
}



