package com.example.yuehaoting.base.diskLruCache.myCache

import android.content.Context
import com.example.yuehaoting.base.diskLruCache.DiskLruCache
import timber.log.Timber
import java.io.*

/**
 * 作者: 天使
 * 时间: 2021/8/23 9:22
 * 描述:
 */
class CacheString {

    private lateinit var diskLruCache: DiskLruCache

    /**
     * 初始化
     */
    fun init(context: Context, folder: String) {

        try {
            diskLruCache = DiskLruCache.open(File(context.externalCacheDir.toString() + "/$folder/"), 1, 1, 15 * 1024 * 1024)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 将字符存入磁盘
     */
    fun putToDisk(key: String, str: String) {
        var os: OutputStream? = null
        try {
            // val snapshot: DiskLruCache.Snapshot = diskLruCache.get(key)
             if (str==""){
                 return
             }
            val editor = diskLruCache.edit(key)

            if (editor != null) {
                os = editor.newOutputStream(0)
                val out = BufferedOutputStream(os, 8 * 1024)

                Timber.v("putToDiskListUrl:%s", str)

                out.write(str.toByteArray())

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
    fun getFromDisk(key: String): String? {

        Timber.v("getFromDisk:%s", key)
        val list: String

        val `is`: InputStream?

        val snapshot: DiskLruCache.Snapshot? = diskLruCache.get(key)
        Timber.v("getFromDisk:%s", snapshot)
        if (snapshot == null) {
            Timber.v("getFromDiskListUrlNull:%s", key)
            return null

        }

        `is` = snapshot.getInputStream(0)
        val bis = BufferedInputStream(`is`)
        val reader = BufferedReader(InputStreamReader(bis))
/*        if (reader.readLine()==null){
            Timber.v("reader.readLine()==null")
        }*/
        list = reader.readLine()



        reader.close()
        bis.close()
        `is`?.close()


        return list
    }

    /**
     * 关闭流
     */
    fun close() {
        diskLruCache.close()
    }

}