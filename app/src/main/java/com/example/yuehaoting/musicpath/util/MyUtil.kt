package com.example.yuehaoting.musicpath.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import com.example.yuehaoting.App.Companion.context
import timber.log.Timber


/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/10 17:41
 * 描述: 我的工具类
 */
class MyUtil {


    /**
     * 判断app是否运行在前台
     */
    fun isAppOnForeground(): Boolean {
        try {
            val activityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val packageName = context.packageName
            var appProcesses: List<RunningAppProcessInfo>? = null
            if (activityManager != null) {
                appProcesses = activityManager.runningAppProcesses
            }
            if (appProcesses == null) {
                return false
            }
            for (appProcess in appProcesses) {
                if (appProcess.processName == packageName &&
                    appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                ) {
                    return true
                }
            }
        } catch (e: Exception) {
            Timber.w("是前台应用, ex: %s", e.message)
            return context.isAppForeground
        }
        return false
    }


}