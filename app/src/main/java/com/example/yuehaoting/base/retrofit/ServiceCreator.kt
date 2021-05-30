package com.example.yuehaoting.base.retrofit


import com.example.yuehaoting.base.DataUri
import com.example.yuehaoting.base.retrofit.interceptor.HttpInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    //创建客服端
    private fun getClient():OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(HttpInterceptor()).build()
    }
    private val retrofit = Retrofit.Builder()
        .client(getClient())
        .baseUrl(DataUri.kuGouApiSearchFor)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

}
