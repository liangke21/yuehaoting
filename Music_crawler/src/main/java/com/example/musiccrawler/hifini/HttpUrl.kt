package com.example.musiccrawler.hifini

import android.os.Messenger
import android.util.Log
import com.example.musiccrawler.hifini.JsoupS.jsoupSHiFiNiSearch
import com.example.musiccrawler.hifini.JsoupS.jsoupSHiFiNiThread
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 作者: 天使
 * 时间: 2021/7/11 13:21
 * 描述:
 */
object HttpUrl {

      fun hiFiNiSearch(Keyword:String,replyTo: Messenger){
          var conn: HttpURLConnection?
              thread {
                  try {


                  val response = StringBuffer()
                  Log.e("执行了吗","你愁啥1")
                  val url = URL("https://hifini.com/search-$Keyword.htm")

                  conn = url.openConnection() as HttpURLConnection
                  conn?.connectTimeout = 8000
                  conn?.readTimeout = 8000
                  Log.e("执行了吗","你愁啥2")
                  conn?.setRequestProperty("User-Agent", "PostmanRuntime/7.28.0")
                  conn?.setRequestProperty(
                      "Cookie",
                      "bbs_sid=raktoit60eu0q3et7dav3drk1r; cookie_test=favmLHKdChmrm0spD7uGSEaVYYPBVLO_2BB7n2V7MP1tP9btZH"
                  )
                  conn?.doInput = true
                  conn?.connect()
                  Log.e("执行了吗","你愁啥3")
                  val `in` = conn?.inputStream
                  val bffR = BufferedReader(InputStreamReader(`in`))

                  bffR.use {

                      bffR.forEachLine {
                          response.append(it)
                      }
                  }
                 Log.e("执行了吗","你愁啥4")
                  jsoupSHiFiNiSearch(response.toString(),replyTo)
                  bffR.close()
                  `in`?.close()
                  }catch (e: IOException){
                      e.printStackTrace()


                  }
              }
      }



    fun hiFiNiSearchPage(page:String,replyTo: Messenger){
        var conn: HttpURLConnection?
        try {
            thread {
                val response = StringBuffer()

                val url = URL("https://hifini.com/$page")

                conn = url.openConnection() as HttpURLConnection
                conn?.connectTimeout = 8000
                conn?.readTimeout = 8000

                conn?.setRequestProperty("User-Agent", "PostmanRuntime/7.28.0")
                conn?.setRequestProperty(
                    "Cookie",
                    "bbs_sid=raktoit60eu0q3et7dav3drk1r; cookie_test=favmLHKdChmrm0spD7uGSEaVYYPBVLO_2BB7n2V7MP1tP9btZH"
                )
                conn?.doInput = true
                conn?.connect()

                val `in` = conn?.inputStream
                val bffR = BufferedReader(InputStreamReader(`in`))

                bffR.use {

                    bffR.forEachLine {
                        response.append(it)
                    }
                }

                jsoupSHiFiNiSearch(response.toString(),replyTo)
                bffR.close()
                `in`?.close()

            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }




}