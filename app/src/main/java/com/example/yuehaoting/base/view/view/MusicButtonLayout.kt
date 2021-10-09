package com.example.yuehaoting.base.view.view


import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
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
        val protruding= MusicButtonProtruding(context, attrs)
        this.addView(protruding)
        val lp1=LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
            lp1.gravity=Gravity.CENTER

        mTasksView = MusicButtonView(context, attrs)
        mTasksView!!.layoutParams=lp1


        musicButtonViewInnerCircle = MusicButtonViewInnerCircle(context, attrs)
        musicButtonViewInnerCircle.layoutParams=lp1

        init()
        this.addView(mTasksView)
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

    }

    /**
     * 设置进度条当前值
     * @param Progress Int
     */
    fun setProgress(Progress: Int){
        mTasksView?.setProgress(Progress)
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
            }
            pause -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    objectAnimator.resume()
                }
                //动画重新开始
                state = playing
                jumpOut = false
            }
            playing -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    objectAnimator.pause()
                }
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
        val stop=4
        when (int) {
            start -> {
                objectAnimator.start() //动画开始

            }
            resume -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    objectAnimator.resume()
                }
                //动画重新开始

            }
            pause -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    objectAnimator.pause()
                }
                //动画暂停
            }
            stop -> {
                objectAnimator.end()
            }
        }
    }


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




}
