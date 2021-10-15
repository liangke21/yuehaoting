package com.example.yuehaoting

import androidx.multidex.MultiDex
import com.example.yuehaoting.Test.test
import com.example.yuehaoting.base.application.InitApplication
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.kotlin.launchMy
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.searchFor.livedata.Repository
import com.example.yuehaoting.util.phoneAttributes.NetworkConnectionJudgment.isNetWork
import com.example.yuehaoting.util.phoneAttributes.NetworkConnectionJudgment.isOnline
import com.example.yuehaoting.util.phoneAttributes.ScreenProperties
import com.example.yuehaoting.util.phoneAttributes.ScreenUtils
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/10 10:37
 * 描述:
 */
class App : InitApplication() {

  var width = 0
   var height = 0

    //前台活动计数器
    private var foregroundActivityCount = 0
    override fun onCreate() {
        super.onCreate()
        context = this
        test()
     val display=ScreenProperties.phoneAttributes(this)
        width=display[0]
        height=display[1]
        isNetWork(context)
        isOnline(context)

        //兼容 5.0 一下版本
        MultiDex.install(this);

    }



    val isAppForeground: Boolean
        get() = foregroundActivityCount > 0

   companion object {
        @JvmStatic
        lateinit var context: App
            private set
    }


}