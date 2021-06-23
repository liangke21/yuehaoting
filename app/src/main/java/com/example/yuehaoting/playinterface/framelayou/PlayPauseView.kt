package com.example.yuehaoting.playinterface.framelayou

import android.animation.Animator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.example.yuehaoting.R

/**
 * 作者: 天使
 * 时间: 2021/6/23 10:04
 * 描述:
 */
class PlayPauseView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {


    private val color: Property<PlayPauseView, Int> = object : Property<PlayPauseView, Int>(Int::class.java, "color") {
        override fun get(v: PlayPauseView): Int? {
            return v.getColor()
        }

        override fun set(v: PlayPauseView, value: Int) {
            v.setColor(value)
        }
    }

    //播放暂停动画持续时间
private val playPauseAnimationDuration = 250L
    //播放暂停绘制类
    private lateinit var mDrawable: PlayPauseDrawable
    //画
    private val mPaint=Paint()
    //动画
    private lateinit var mAnimator: Animator
    private var mBackgroundColor = 0
    private var mWidth = 0
    private var mHeight = 0
    init {
        setWillNotDraw(false)
        mBackgroundColor=resources.getColor(R.color.white)
        mPaint.isAntiAlias=true
        mPaint.style=Paint.Style.FILL
        mPaint.color = mBackgroundColor
        mDrawable= PlayPauseDrawable(context)
        mDrawable.callback=this
    }

    /**
     * 设置尺寸大小
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawable.setBounds(0,0,w,h)
        mWidth=w
        mHeight=h

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            outlineProvider= object : ViewOutlineProvider(){
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0,0,view.width,view.height)
                }
            }
            clipToOutline=true
        }
    }

    private fun setColor(value: Int) {
     mBackgroundColor=value
    }

    private fun getColor(): Int {
       return mBackgroundColor
    }

    /**
     * 验证是否可绘制
     */
    override fun verifyDrawable(who: Drawable): Boolean {
        return who==mDrawable || super.verifyDrawable(who)
    }

    /**
     * 绘制背景画布
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPaint.color = mBackgroundColor
        val radius= mWidth.coerceAtMost(mHeight) /2f
        canvas?.drawCircle(mWidth/2f,mHeight/2f,radius,mPaint)
        mDrawable.draw(canvas!!)
    }

    override fun setBackgroundColor(color: Int) {
        mBackgroundColor=color
        mPaint.color=color
        invalidate()
    }

    /**
     * 显示播放暂停动画
     */
    fun initState(isPlay:Boolean){
        if (isPlay){
            mDrawable.setPlay()
        }else{
            mDrawable.setPause()
        }
    }
    fun updateStRte(isPlay: Boolean,withAnim:Boolean){
        if (mDrawable.isPlay != isPlay){
            return
        }
        toggle(withAnim)
    }

    private fun toggle(withAnim: Boolean) {
        if (withAnim){
            mAnimator.cancel()
            mAnimator=mDrawable.pausePlayAnimator
            mAnimator.interpolator = DecelerateInterpolator()
            mAnimator.duration = playPauseAnimationDuration
            mAnimator.start()

        }else{
            val isPlay=mDrawable.isPlay
            initState(isPlay)
            invalidate()
        }

    }

}