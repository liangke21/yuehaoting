package com.example.musiccrawler.hifini

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Messenger
import android.util.Log
import com.example.musiccrawler.base.ServiceHandler

class HiginioService : Service() {


    private var mServiceHandler :ServiceHandler?= null

    private var mMessage:Messenger ?= null
    override fun onBind(intent: Intent): IBinder? {
        mServiceHandler = ServiceHandler()

        mMessage=Messenger(mServiceHandler)
        Log.e("HiginioService","主进程绑定")
      return mMessage?.binder
    }

    override fun onCreate() {
        super.onCreate()
        //hiFiNiThread()
    }

    override fun onDestroy() {
        Log.e("HiginioService","主进程销毁")
        super.onDestroy()
        mMessage=null
        mServiceHandler=null
    }
}