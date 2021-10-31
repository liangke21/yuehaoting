package com.example.yuehaoting.base.viewGroup

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * 作者: LiangKe
 * 时间: 2021/10/31 16:58
 * 描述:
 */
 class MyFrameLayout: FrameLayout {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {



        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(l: OnTouchListener?) {

        super.setOnTouchListener(l)
    }

    /**
     * 触摸拦截  true 不像下一层传递
     * @param event MotionEvent
     * @return Boolean
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
}