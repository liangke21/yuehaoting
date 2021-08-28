package com.example.yuehaoting.base.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.yuehaoting.base.gestureDetector.SmGestureDetectorSimpleOnGestureListener
import com.example.yuehaoting.theme.StatusBarUtil
import com.example.yuehaoting.theme.ThemeStore.statusBarColor
import com.example.yuehaoting.util.Tag
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/6/27 14:14
 * 描述:
 */
open class PlayBaseActivity:BaseActivity() {
private lateinit var mGestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gestureSlidingAnimation()
    }
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setSatuBarColor()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setSatuBarColor()
    }

    /**
     * 设置状态栏颜色
     */
    protected open fun setSatuBarColor(){
       StatusBarUtil.setColorNoTranslucent(this, statusBarColor)
    }


    /**
     * 手势滑动动画
     */
    fun  gestureSlidingAnimation(){

         mGestureDetector=GestureDetector(this,object : SmGestureDetectorSimpleOnGestureListener(){


            override fun onDown(e: MotionEvent?): Boolean {
                return super.onDown(e)
            }

            override fun onShowPress(e: MotionEvent?) {
                super.onShowPress(e)
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return super.onSingleTapUp(e)
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onLongPress(e: MotionEvent?) {
                super.onLongPress(e)
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                return super.onFling(e1, e2, velocityX, velocityY)
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                return super.onSingleTapConfirmed(e)
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                return super.onDoubleTap(e)
            }

            override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
                return super.onDoubleTapEvent(e)
            }

            override fun onContextClick(e: MotionEvent?): Boolean {
                return super.onContextClick(e)
            }
        })

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mGestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)

    }



}