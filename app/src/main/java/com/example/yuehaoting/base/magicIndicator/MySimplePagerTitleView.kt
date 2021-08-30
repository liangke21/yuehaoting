package com.example.yuehaoting.base.magicIndicator

import android.content.Context
import android.view.Gravity
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 * 作者: 天使
 * 时间: 2021/8/30 9:51
 * 描述:
 */
class MySimplePagerTitleView(context: Context) : SimplePagerTitleView(context) {

    override fun onSelected(index: Int, totalCount: Int) {
        super.onSelected(index, totalCount)

        textSize = 25f
        gravity=Gravity.BOTTOM
    }

}