package com.example.yuehaoting.base.retrofit


import android.util.Log
import com.example.yuehaoting.base.retrofit.interceptor.HttpInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceCreator(api: String) {

/*    private val loggingInterceptor= HttpLoggingInterceptor {
            message -> Log.i("TEST", "ApiServiceHelper.createApiService().HttpLoggingInterceptor.log().message -> $message");
    }
    private fun getLogger(): OkHttpClient {
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return  OkHttpClient.Builder()
          .addInterceptor(loggingInterceptor)
          .build()
  }*/

    //创建客服端
    private fun getClient(): OkHttpClient {

        return OkHttpClient.Builder().addInterceptor(HttpInterceptor()).build()
    }

    private val retrofit = Retrofit.Builder()
        .client(getClient())
        .baseUrl(api)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)


    private var retrofitHtml: Retrofit = Retrofit.Builder()
        .baseUrl(api)
        .client( getClient())
        .build()

    fun <T> createHtml(serviceClass: Class<T>): T = retrofitHtml.create(serviceClass)
}

