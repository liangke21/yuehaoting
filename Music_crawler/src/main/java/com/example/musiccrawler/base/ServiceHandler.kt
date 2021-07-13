package com.example.musiccrawler.base

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log


/**
 * 作者: 天使
 * 时间: 2021/7/13 14:47
 * 描述:
 */
class ServiceHandler: Handler(Looper.getMainLooper()){

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when(msg.what){
                    1->{
                        val bundle=msg.data

                        bundle.classLoader=javaClass.classLoader

                        Log.e("接收到了主进程传来的数据",bundle.getString("Single").toString())
                    }

        }

    }


}