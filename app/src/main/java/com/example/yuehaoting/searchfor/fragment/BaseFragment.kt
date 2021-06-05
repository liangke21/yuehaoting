package com.example.yuehaoting.searchfor.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 17:13
 * 描述:
 */
abstract class BaseFragment : MyFragment() {

private var isLoaded=false

    override fun onResume() {
        super.onResume()
        if (!isLoaded && !isHidden){
            Log.d(TAG, "lazyInit:!!!!!!!")
            lazyInit()
            isLoaded=true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded=false
    }

    abstract fun lazyInit()
}