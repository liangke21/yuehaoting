package com.example.yuehaoting.base.gestureDetector

import android.view.GestureDetector
import android.view.MotionEvent
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/8/24 15:02
 * 描述:
 */
open class SmGestureDetectorSimpleOnGestureListener: GestureDetector.SimpleOnGestureListener() {

    override fun onDown(e: MotionEvent?): Boolean {
        Timber.v("滑动:%s",e)
        return super.onDown(e)
    }

    override fun onShowPress(e: MotionEvent?) {
        Timber.v("滑动:%s",e)
        super.onShowPress(e)
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Timber.v("滑动:%s",e)
        return super.onSingleTapUp(e)
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    override fun onLongPress(e: MotionEvent?) {
        Timber.v("滑动:%s",e)
        super.onLongPress(e)
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        Timber.v("滑动:%s",e1)
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        Timber.v("滑动:%s",e)
        return super.onSingleTapConfirmed(e)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        Timber.v("滑动:%s",e)
        return super.onDoubleTap(e)
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        Timber.v("滑动:%s",e)
        return super.onDoubleTapEvent(e)
    }

    override fun onContextClick(e: MotionEvent?): Boolean {
        Timber.v("滑动:%s",e)
        return super.onContextClick(e)
    }
}