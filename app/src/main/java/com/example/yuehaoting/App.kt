package com.example.yuehaoting

import android.app.Application
import com.example.musiccrawler.hifini.HttpUrl.hiFiNiThread
import com.example.yuehaoting.base.application.InitApplication
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.data.music163.PostMusic
import com.example.yuehaoting.kotlin.launchMy
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/10 10:37
 * 描述:
 */
class App : InitApplication() {


    //前台活动计数器
    private var foregroundActivityCount = 0
    override fun onCreate() {
        super.onCreate()
        context = this
       test()
    }



    val isAppForeground: Boolean
        get() = foregroundActivityCount > 0
    companion object{
        @JvmStatic
        lateinit var context: App
            private set
    }
    private fun test() {
        launchMy {
           // val a=   SongNetwork.songList(p)
            val b=SongNetwork.songList("可不可以勇敢一点","name","netease",1)
             Timber.v("aaaaaaaaaa:%s",b,)
        }

    }
}