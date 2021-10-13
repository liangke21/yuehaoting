package com.example.yuehaoting.util.phoneAttributes



import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService


/**
 * 作者: LiangKe
 * 时间: 2021/10/12 18:37
 * 描述:
 */
object NetworkConnectionJudgment {



    private const val DEBUG_TAG = "NetworkStatusExample"


    fun isNetWork(context: Context){
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn: Boolean = false
        var isMobileConn: Boolean = false
        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network)?.apply {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn = isWifiConn or isConnected
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected
                }
            }
        }
        Log.d(DEBUG_TAG, "Wifi 是否连接: $isWifiConn")
        Log.d(DEBUG_TAG, "移动数据 是否连接: $isMobileConn")
    }

    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        Log.d(DEBUG_TAG, "网络接口是否可用: ${networkInfo?.isConnected == true}")
        return networkInfo?.isConnected == true
    }

}