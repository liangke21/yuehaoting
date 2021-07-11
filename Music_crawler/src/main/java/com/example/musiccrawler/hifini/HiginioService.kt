package com.example.musiccrawler.hifini

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.example.musiccrawler.AidlInterfaceMy
import com.example.musiccrawler.hifini.HttpUrl.hiFiNiSearch
import com.example.musiccrawler.hifini.HttpUrl.hiFiNiThread
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class HiginioService : Service() {

private val mStud= object :AidlInterfaceMy.Stub(){


    override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
    }
}

    override fun onBind(intent: Intent): IBinder {
      return mStud
    }

    override fun onCreate() {
        super.onCreate()
        //hiFiNiThread()
        hiFiNiSearch("六哲")
    }

}