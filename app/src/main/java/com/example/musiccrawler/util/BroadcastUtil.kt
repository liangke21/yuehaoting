package com.example.musiccrawler.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musiccrawler.App
import com.example.musiccrawler.App.Companion.context
import timber.log.Timber


/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/10 17:41
 * 描述: 我的工具类
 */
class BroadcastUtil {
    /**
     * 注册本地Receiver
     */
    fun registerLocalReceiver(receiver: BroadcastReceiver,filter: IntentFilter){
        LocalBroadcastManager.getInstance(App.context).registerReceiver(receiver,filter)
    }

    fun unregisterLocalReceiver(receiver: BroadcastReceiver){
        LocalBroadcastManager.getInstance(App.context).unregisterReceiver(receiver)
    }
    /**
     * 发送本地广播
     */
    fun sendLocalBroadcast(intent: Intent){
    LocalBroadcastManager.getInstance(App.context).sendBroadcast(intent)
}

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