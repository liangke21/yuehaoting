package com.example.yuehaoting.loginRegistered.retrofit

import com.example.yuehaoting.loginRegistered.entity.MNumber
import com.example.yuehaoting.loginRegistered.entity.Success
import com.example.yuehaoting.loginRegistered.entity.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 作者: QQ群:1396797522
 * 时间: 2021/5/26 15:18
 * 描述:
 */
interface LoginService {

    @POST("create/login")
    fun addData(@Body user: User): Call< Success>
   @GET("create/login/number/number?")
    fun inquire(@Query("number")number:String):Call<MNumber>
}