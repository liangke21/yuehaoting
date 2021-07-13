package com.example.musiccrawler.hifini

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.musiccrawler.AidlInterfaceMy
import com.example.musiccrawler.hifini.HttpUrl.hiFiNiSearch
import com.example.musiccrawler.hifini.JsoupS.gson

class HiginioService : Service() {

private val mStud= object : AidlInterfaceMy.Stub(){


    override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {

    }

    override fun keyword(Keyword: String?) {
        if (Keyword != null) {
            hiFiNiSearch(Keyword)
        }
    }



    override fun onCreate(): String? {
        Log.v("运行了吗2","我靠运行了")
      return gson
    }
}

    override fun onBind(intent: Intent): IBinder {
      return mStud
    }

    override fun onCreate() {
        super.onCreate()
        //hiFiNiThread()

    }

}