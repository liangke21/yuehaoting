package com.example.musiccrawler.base.sevice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/15 13:51
 * 描述:
 */
open class SmService:Service() {
    override fun onBind(intent: Intent): IBinder? {
        Timber.v("绑定服务")
       return null
    }

    override fun onCreate() {
        super.onCreate()
        Timber.v("创建服务")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.v("开始服务")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Timber.v("销毁服务")
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.v("解绑服务")
        return super.onUnbind(intent)
    }
}