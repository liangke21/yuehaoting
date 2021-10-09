package com.example.yuehaoting.base.view.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.yuehaoting.R

/**
 * 作者: LiangKe
 * 时间: 2021/10/8 18:41
 * 描述:
 */
class MusicButtonProtruding : View {
    //凸出圆背景
    private var protruding = Paint()
    // 圆心x坐标
    private var mXCenter = 0
    // 圆心y坐标
    private var mYCenter = 0
    //背景
    private var backgroundL=0

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typeArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TasksCompletedView, 0, 0
        )
        backgroundL =typeArray.getColor(R.styleable.TasksCompletedView_backgroundL,-0x1)
        initVariable()
    }


    //初始化画笔
    private fun initVariable() {

        //外圆凸出背景
        protruding.isAntiAlias = true
        protruding.color=backgroundL

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mXCenter = width / 2
        mYCenter = height / 2

        canvas.drawCircle(
            (mXCenter).toFloat(),
            (mXCenter).toFloat(),
            (mXCenter).toFloat(),
            protruding
        )

    }




}