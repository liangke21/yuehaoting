package com.example.musiccrawler.base

import android.os.*
import android.util.Log
import com.example.musiccrawler.hifini.HttpUrl.hiFiNiSearch


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
                        val keyword=bundle.getString("Single").toString()
                        hiFiNiSearch(keyword,msg.replyTo)
                        Log.e("接收到了主进程传来的数据",keyword)
                    }

                   2->{
                       val client=msg.replyTo
                       val bundle=msg.data
                       bundle.classLoader=javaClass.classLoader
                       val json=bundle.getString("json").toString()
                       Log.e("接收到异步请求数据",json)
                       val replyMessage=Message.obtain(null,100)

                       val data=Bundle()
                       data.putString("json",json)
                       replyMessage.data=data
                       try {
                           client.send(replyMessage)
                       }catch (e:RemoteException){
                           e.printStackTrace()
                       }

                   }
        }

    }


}