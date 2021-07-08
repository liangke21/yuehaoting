package com.example.yuehaoting.playInterface.activity

import android.content.Context
import android.util.Log
import com.example.yuehaoting.base.diskLruCache.DiskLruCache
import timber.log.Timber
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * 作者: 天使
 * 时间: 2021/7/7 14:25
 * 描述:
 */
class CacheUrl {
    private lateinit var diskLruCache: DiskLruCache

    /**
     * 初始化
     */
    fun init(context: Context) {

        try {
            diskLruCache = DiskLruCache.open(File(context.externalCacheDir.toString() + "/Url/"), 1, 1, 15 * 1024 * 1024)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 将字符存入磁盘
     */
    fun putToDisk(key: String, list: ArrayList<String>) {
        var os: OutputStream? = null
        try {
            // val snapshot: DiskLruCache.Snapshot = diskLruCache.get(key)

            val editor = diskLruCache.edit(key)

            if (editor != null) {
                os = editor.newOutputStream(0)
                val out = BufferedOutputStream(os, 8 * 1024)

                list.forEach {
                    Timber.v("putToDiskListUrl:%s", it)
                    val st = it + "\n"
                    out.write(st.toByteArray())
                }
                editor.commit()
                out.close()


            }

            os?.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 获取磁盘数据
     */
    fun getFromDisk(key: String): ArrayList<String>? {

        Timber.v("getFromDisk:%s", key)
        val list = ArrayList<String>()

        val snapshot: DiskLruCache.Snapshot? = null

        var `is`: InputStream? = null

        snapshot = diskLruCache.get(key)

        if (snapshot == null) {
            Timber.v("getFromDiskListUrlNull:%s", key)
            return null

        }

        `is` = snapshot.getInputStream(0)

        val bis = BufferedInputStream(`is`)

        val reader = BufferedReader(InputStreamReader(bis))
        var s: String? = null
        while ((reader.readLine().also { s = it }) != null) {
            Timber.v("getFromDiskListUrl:%s", s.toString())
            list.add(s.toString())
        }


        reader.close()
        bis.close()
        `is`?.close()


        return list
    }


}