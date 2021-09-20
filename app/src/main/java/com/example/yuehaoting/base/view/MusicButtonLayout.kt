package com.example.yuehaoting.base.view


import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout


/**
 * 作者: LiangKe
 * 时间: 2021/9/18 13:31
 * 描述:
 */
class MusicButtonLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private var mTotalProgress = 0

    private var mCurrentProgress = 0

    //进度条
    private var mTasksView: MusicButtonView? = null

    //内圆
    private lateinit var musicButtonViewInnerCircle: MusicButtonViewInnerCircle

    private lateinit var objectAnimator: ObjectAnimator
    private val playing = 1 //正在播放

    private val pause = 2 //暂停

    private val stop = 3 //停止

    var state = 0

    private var jumpOut = false

    init {
        mTasksView = MusicButtonView(context, attrs)
        musicButtonViewInnerCircle = MusicButtonViewInnerCircle(context, attrs)
        init()
    }



    /**
     *  是否显示字体
     * @param isDisplayText Boolean
     */
    fun isDisplayText(isDisplayText:Boolean){
        mTasksView?.isDisplayText(isDisplayText)
    }
    /**
     * 进度条总值
     * @param TotalProgress Int
     */
    fun setTotalProgress(TotalProgress: Int) {
        this.mTotalProgress = TotalProgress
        mTasksView?.setTotalProgress(TotalProgress)
        //Thread(ProgressRunnable()).start()
        this.addView(mTasksView)
    }

    /**
     * 设置圆内图片
     * @param bitmap Bitmap
     */
    fun setBitmap(bitmap: Bitmap){
        musicButtonViewInnerCircle.setBitmap(bitmap)
    }
    private fun init() {
        state = playing
        objectAnimator = ObjectAnimator.ofFloat(
            musicButtonViewInnerCircle,
            "rotation",
            0f,
            360f
        ) //添加旋转动画，旋转中心默认为控件中点

        objectAnimator.duration = 10000 //设置动画时间  也就是旋转时间

        objectAnimator.interpolator = LinearInterpolator() //动画时间线性渐变

        objectAnimator.repeatCount = ObjectAnimator.INFINITE
        objectAnimator.repeatMode = ObjectAnimator.RESTART
        this.addView(musicButtonViewInnerCircle)
    }

    /**
     * 开始动画
     */
    fun playMusic() {
        when (state) {
            stop -> {
                objectAnimator.start() //动画开始
                state = playing
                jumpOut = false
                Thread(ProgressRunnable()).start()
            }
            pause -> {
                objectAnimator.resume()
                //动画重新开始
                state = playing
                jumpOut = false
                Thread(ProgressRunnable()).start()
            }
            playing -> {
                objectAnimator.pause()
                //动画暂停
                state = pause
                jumpOut = true
            }
        }
    }

    /**
     * 开始动画
     */
    fun playMusic(int: Int) {
        val start=1
        val resume=2
        val pause=3
        when (int) {
            start -> {
                objectAnimator.start() //动画开始
                jumpOut = false
                Thread(ProgressRunnable()).start()
            }
            resume -> {
                objectAnimator.resume()
                //动画重新开始
                jumpOut = false
                Thread(ProgressRunnable()).start()
            }
            pause -> {
                objectAnimator.pause()
                //动画暂停
                jumpOut = true
            }
        }
    }
    /**
     * 暂停动画
     */


    /**
     * 结束动画
     */
    fun stopMusic() {
        objectAnimator.end() //动画结束
        state = stop
        jumpOut = true
        mCurrentProgress = 0
        mTasksView?.setProgress(mCurrentProgress)
    }

    inner class ProgressRunnable : Runnable {
        override fun run() {

            while (mCurrentProgress < mTotalProgress) {

                if (jumpOut) {
                    break
                }
                mCurrentProgress += 1

                mTasksView?.setProgress(mCurrentProgress)
                Log.d("$mTotalProgress", mCurrentProgress.toString())
                if (mCurrentProgress == mTotalProgress){

                    Handler(Looper.getMainLooper()).post {
                        stopMusic()
                    }
                }
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }


    override fun getAccessibilityClassName(): CharSequence {
        return super.getAccessibilityClassName()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun setForegroundGravity(foregroundGravity: Int) {
        super.setForegroundGravity(foregroundGravity)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return super.checkLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return super.generateLayoutParams(attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return super.generateLayoutParams(lp)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return super.generateDefaultLayoutParams()
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return super.shouldDelayChildPressedState()
    }

    override fun setMeasureAllChildren(measureAll: Boolean) {
        super.setMeasureAllChildren(measureAll)
    }

    override fun getConsiderGoneChildrenWhenMeasuring(): Boolean {
        return super.getConsiderGoneChildrenWhenMeasuring()
    }

    override fun getMeasureAllChildren(): Boolean {
        return super.getMeasureAllChildren()
    }
}
