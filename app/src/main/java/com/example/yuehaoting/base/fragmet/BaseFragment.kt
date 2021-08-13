package com.example.yuehaoting.base.fragmet

import android.util.Log

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 17:13
 * 描述:
 */
abstract class BaseFragment : MyFragment() {


    private var isLoaded = false

    override fun onResume() {

        super.onResume()
        Log.d(TAG, "lazyInit:!!!!!!! isLoaded=$isLoaded,isHidden=$isHidden")
        if (!isLoaded && !isHidden) {
            lazyInit()
            isLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
    }

    abstract fun lazyInit()

}