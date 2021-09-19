package com.example.yuehaoting.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.yuehaoting.R

/**
 * 作者: LiangKe
 * 时间: 2021/9/18 16:12
 * 描述:
 */
class MusicButtonViewInnerCircle : View {
    // 画实心圆的画笔
    private var mCirclePaint = Paint()

    // 圆环宽度
    private var mStrokeWidth = 0f

    // 圆心x坐标
    private var mXCenter = 0

    // 圆形颜色
    private var mCircleColor = 0
    // 圆心y坐标
    private var mYCenter = 0

    private lateinit var bitmap: Bitmap

    //是否设置图片
    private var isBitmap = false

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typeArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TasksCompletedView, 0, 0
        )

            mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidthL, 10f)
            mCircleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, -0x1)

        initVariable()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //初始化画笔
    private fun initVariable() {
        //内圆

        mCirclePaint.isAntiAlias = true
        mCirclePaint.color = mCircleColor
        mCirclePaint.style = Paint.Style.FILL
    }
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        mXCenter = width / 2
        mYCenter = height / 2
        Log.e("MusicButtonView", "$width,  $height  $isBitmap")
        //内圆
        try {
            if (!isBitmap) {
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.play_activity_album)
            }
            val thumbImg = Bitmap.createScaledBitmap(bitmap, width, height, true)
            val shader = BitmapShader(thumbImg, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mCirclePaint.shader = shader
        } catch (e: Exception) {
            e.printStackTrace()
        }
        canvas.drawCircle(
            (mXCenter).toFloat(),
            (mXCenter).toFloat(),
            (mXCenter).toFloat(),
            mCirclePaint
        )

    }

    /**
     * 设置圆内图片
     * @param bitmap Bitmap
     */
    fun setBitmap(bitmap: Bitmap) {
        isBitmap = true
        this.bitmap = bitmap
        postInvalidate() //重绘
    }
}