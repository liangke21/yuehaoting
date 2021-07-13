package com.example.yuehaoting.base.thread

import timber.log.Timber


/**
 * 作者: 天使
 * 时间: 2021/7/6 16:29
 * 描述:
 */
class ThreadMy(runnable:Runnable,var tag:String="") :Thread(runnable){


    override fun start() {
        super.start()
       Timber.tag(tag).e("start","线程启动")
    }

    override fun run() {
        Timber.tag(tag).e("线程名称",name)
        super.run()

    }

}