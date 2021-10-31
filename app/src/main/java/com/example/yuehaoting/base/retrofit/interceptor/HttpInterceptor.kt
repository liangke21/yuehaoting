package com.example.yuehaoting.base.retrofit.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * 作者: QQ:1396797522
 * 时间: 2021/2/26 16:22
 * 描述: 拦截器 /重定向 请求头 加密/解密
 */
class HttpInterceptor : Interceptor {
    private val TAG = "HTTP"
    private lateinit var response: Response
    override fun intercept(chain: Interceptor.Chain): Response {
             try{
                 // Timber.e("网络异常4%s", chain.request().)
                 //请求参数
                 val request = chain.request()
                 Log.i("TEST", "MyInterceptor.intercept.request.toString -> $request");

                 response = chain.proceed(request)
                 Log.i("TEST", "请求时长 通过网络传输发起请求之前的时间戳-> ${ response.sentRequestAtMillis()}");


                 Log.i(TAG, "=========拦截==========")
                 if (request.method() == "GET") {
                     Log.i(TAG, request.url().toString())
                 }

                 if (request.method() == "POST") {
                     Log.i(TAG, request.url().toString())
                 }
                 Log.i(TAG, "=========拦截==========")
                 /*   response.body()?.let {
                        Log.i(TAG,it?.string())

                    }*/



             }catch (e: SocketTimeoutException) {
                 Timber.e("套接字超时异常")
                 e.printStackTrace()

             }

       return response

    }


}
