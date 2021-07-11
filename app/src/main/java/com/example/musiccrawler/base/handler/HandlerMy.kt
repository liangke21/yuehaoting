package com.example.musiccrawler.base.handler

import android.os.Handler
import timber.log.Timber


/**
 * 作者: 天使
 * 时间: 2021/7/6 17:11
 * 描述:
 */
class HandlerMy(val tag:String=""): Handler() {
    /**
     * 重复耗时操作
     */

    fun setPostDelayed(r:Runnable , delayMillis:Long ){

        this.postDelayed(r,delayMillis)
        Timber.tag(tag).e( "开启定时器")
    }

    fun setRemoveCallbacks(r:Runnable){
        Timber.tag(tag).e("关闭定时器")
        this.removeCallbacks(r)
    }
}