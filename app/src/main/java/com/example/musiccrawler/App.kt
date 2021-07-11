package com.example.musiccrawler

import android.app.Application

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/10 10:37
 * 描述:
 */
class App : Application() {
    //前台活动计数器
    private var foregroundActivityCount = 0
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    val isAppForeground: Boolean
        get() = foregroundActivityCount > 0
    companion object{
        @JvmStatic
        lateinit var context: App
            private set
    }

}