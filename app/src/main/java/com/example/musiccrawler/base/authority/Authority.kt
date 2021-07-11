package com.example.musiccrawler.base.authority

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.lang.Exception

/**
 * 作者: 天使
 * 时间: 2021/6/27 18:50
 * 描述:
 */
object Authority {
          //读写权限
     fun readAndWritePermissions(activity:Activity) {
        try {
            //检查是否有读写权限
            val permission= ActivityCompat.checkSelfPermission(activity,"android.permission.WRITE_EXTERNAL_STORAGE")
            if (permission!= PackageManager.PERMISSION_GRANTED){
                //没有读写权限
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE" ),1)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}