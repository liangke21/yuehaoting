package com.example.yuehaoting.base.smartRefreshLayout

import android.content.Context
import android.util.AttributeSet
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * 作者: LiangKe
 * 时间: 2021/10/28 13:35
 * 描述:
 */
class MySmartRefreshLayout: SmartRefreshLayout {
init {
    mFloorDuration=1000
}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){

        mReboundDuration=1000
    }
}