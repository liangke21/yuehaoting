package com.example.yuehaoting.base.fragmet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yuehaoting.searchFor.SearchActivity
import com.example.yuehaoting.searchFor.fragment.interfacet.HolderItemView

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 17:13
 * 描述:
 */
abstract class LazyBaseFragment : MyFragment() {


    private var isLoaded = false

    protected lateinit var holderItemView: HolderItemView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        holderItemView=activity as HolderItemView
    }
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