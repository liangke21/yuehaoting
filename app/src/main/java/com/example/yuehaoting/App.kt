package com.example.yuehaoting

import com.example.yuehaoting.base.application.InitApplication
import com.example.yuehaoting.kotlin.launchMy
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.searchFor.livedata.Repository
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

      val a= Repository.music163("偏爱", "name", "netease", 1)




        Timber.v("运行了吗:%s",a.toString() )

    }
}