package com.example.musiccrawler.playInterface.viewpager

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/22 14:12
 * 描述:
 */
class AudioViewPager : ViewPager {

    private var mIntercept = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setIntercept(value: Boolean) {
        mIntercept = value
    }
}

