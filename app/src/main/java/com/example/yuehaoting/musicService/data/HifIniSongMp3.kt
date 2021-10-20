package com.example.yuehaoting.musicService.data

import android.util.Log
import com.example.musiccrawler.hifini.JsoupS
import com.example.yuehaoting.base.retrofit.SongNetwork.hifIniT
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.Buffer
import java.nio.charset.Charset

/**
 * 作者: 天使
 * 时间: 2021/7/13 18:40
 * 描述:
 */
object HifIniSongMp3 {


    suspend fun songIDMp3(fileHash: String): Array<String> {
        Timber.v("HifIniSongMp3:%s", fileHash)
        val response = StringBuffer()
        //异步返回的请求数据
        val string = hifIniT(fileHash).source()

        val buffer = string.buffer()

        val stream: InputStream = buffer.inputStream()
        val bffR = BufferedReader(InputStreamReader(stream))
        bffR.use {

            bffR.forEachLine {
                response.append(it)
            }
        }
        val mp3 = JsoupS.jsoupSHiFiNiThread(response.toString())
        mp3[0]
        Timber.v("HifIniSongMp3:%s", mp3)
      //  return "https://hifini.com/$mp3"
        return arrayOf("https://hifini.com/${mp3[0]}","${mp3[1]}")
    }


}