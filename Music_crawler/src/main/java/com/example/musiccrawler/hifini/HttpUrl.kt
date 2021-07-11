package com.example.musiccrawler.hifini

import com.example.musiccrawler.hifini.JsoupS.jsoupSHiFiNiSearch
import com.example.musiccrawler.hifini.JsoupS.jsoupSHiFiNiThread
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * 作者: 天使
 * 时间: 2021/7/11 13:21
 * 描述:
 */
object HttpUrl {

      fun hiFiNiSearch(Keyword:String){
          var conn: HttpURLConnection?
          try {
              thread {
                  val response = StringBuffer()

                  val url = URL("https://hifini.com/search-$Keyword.htm")

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

                  jsoupSHiFiNiSearch(response.toString())
                  bffR.close()
                  `in`?.close()

              }
          }catch (e:Exception){
              e.printStackTrace()
          }


      }

    fun hiFiNiThread(){
        var conn: HttpURLConnection?
        try {
            thread {
                    val response = StringBuffer()

                    val url = URL("https://hifini.com/thread-90.htm")

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
                    println(response.toString())
                    jsoupSHiFiNiThread(response.toString())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }


    }


}