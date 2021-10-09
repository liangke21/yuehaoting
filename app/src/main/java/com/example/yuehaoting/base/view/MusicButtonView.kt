package com.example.yuehaoting.base.view


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.yuehaoting.R
import kotlin.math.ceil

/**
 * 作者: LiangKe
 * 时间: 2021/9/17 22:33
 * 描述:
 */
class MusicButtonView : View {
    // 画实心圆的画笔
    private var mCirclePaint = Paint()

    // 画圆环的画笔
    private var mRingPaint = Paint()

    // 画圆环的画笔背景色
    private var mRingPaintBg = Paint()

    // 画字体的画笔
    private var mTextPaint = Paint()


    // 圆环颜色
    private var mRingColor = 0

    // 圆环背景颜色
    private var mRingBgColor = 0

    // 字体大小
    private var textSize = 0f

    // 圆环背景宽度
    private var mStrokeWidthBackground = 0f

    // 圆环宽度
    private var mStrokeWidth = 0f

    // 圆心x坐标
    private var mXCenter = 0

    // 圆心y坐标
    private var mYCenter = 0

    // 字的长度
    private var mTxtWidth = 0f

    // 字的高度
    private var mTxtHeight = 0f

    // 总进度
    private var mTotalProgress = 0

    // 当前进度
    private var mProgress = 0

    //是否开启字体显示 默认开启
    private var isDisplayText = true

    //凸出圆背景
    private var protruding = Paint()

    private var bulgeRadius = 10f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
        initVariable()

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //属性
    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val typeArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TasksCompletedView, 0, 0
        )
        textSize = typeArray.getDimension(R.styleable.TasksCompletedView_textSizeL, 80f)
        mStrokeWidthBackground = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidthBackground, 10f)
        mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidthL, 10f)
        mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, -0x1)
        mRingBgColor = typeArray.getColor(R.styleable.TasksCompletedView_ringBgColor, -0x1)
        bulgeRadius = typeArray.getDimension(R.styleable.TasksCompletedView_bulgeRadius, 10f)
    }

    //初始化画笔
    private fun initVariable() {

        //外圆凸出背景

        protruding.isAntiAlias = true
        protruding.style = Paint.Style.STROKE
        protruding.strokeWidth = bulgeRadius


        //外圆弧背景

        mRingPaintBg.isAntiAlias = true
        mRingPaintBg.color = mRingBgColor
        mRingPaintBg.style = Paint.Style.STROKE
        mRingPaintBg.strokeWidth = mStrokeWidthBackground


        //外圆弧
        mRingPaint.isAntiAlias = true
        mRingPaint.color = mRingColor
        mRingPaint.style = Paint.Style.STROKE
        mRingPaint.strokeWidth = mStrokeWidth
        mRingPaint.strokeCap = Paint.Cap.ROUND   //设置线冒样式，有圆 有方

        //中间字
        mTextPaint.isAntiAlias = true
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.color = mRingColor
        mTextPaint.textSize = textSize
        val fm = mTextPaint.fontMetrics
        mTxtHeight = ceil((fm.descent - fm.ascent).toDouble()).toInt().toFloat()

    }


    //画图
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        mXCenter = width / 2
        mYCenter = height / 2
        Log.e("MusicButtonView", "$width,  $height  $mStrokeWidth")

        //外圆弧背景
        val oval1 = RectF()
        oval1.left = mStrokeWidthBackground / 2
        oval1.top = mStrokeWidthBackground / 2
        oval1.right = width - (mStrokeWidthBackground / 2)
        oval1.bottom = height - (mStrokeWidthBackground / 2)
        canvas.drawArc(oval1, 0F, 360F, false, mRingPaintBg) //圆弧所在的椭圆对象、圆弧的起始角度、圆弧的角度、是否显示半径连线

        //外圆弧
        if (mProgress > 0) {
            val oval = RectF()
            oval.left = mStrokeWidth / 2
            oval.top = mStrokeWidth / 2
            oval.right = width - (mStrokeWidth / 2)
            oval.bottom = height - (mStrokeWidth / 2)
            canvas.drawArc(
                oval,
                -90F,
                mProgress.toFloat() / mTotalProgress * 360,
                false,
                mRingPaint
            )

            //字体
            if (isDisplayText) {
                val txt = mProgress.toString() + "秒"
                mTxtWidth = mTextPaint.measureText(txt, 0, txt.length)
                canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint)
            }
        }


    }

    /**
     * 设置总进度
     * @param totalProgress Int
     */
    fun setTotalProgress(totalProgress: Int) {
        this.mTotalProgress = totalProgress
    }

    //设置进度变化
    fun setProgress(progress: Int) {
        mProgress = progress
        postInvalidate() //重绘
    }

    /**
     * 是否开启字体显示
     * @param isDisplayText Boolean
     */
    fun isDisplayText(isDisplayText: Boolean) {
        this.isDisplayText = isDisplayText
    }

}