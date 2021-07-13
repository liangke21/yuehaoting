package com.example.musiccrawler.hifini

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Messenger
import android.util.Log
import com.example.musiccrawler.base.ServiceHandler

class HiginioService : Service() {


    private val mServiceHandler= ServiceHandler()

    var mMessage=Messenger(mServiceHandler)
    override fun onBind(intent: Intent): IBinder {
        Log.e("yuehaotingHiginioService","主进程绑定")
      return mMessage.binder
    }

    override fun onCreate() {
        super.onCreate()
        //hiFiNiThread()
    }

}