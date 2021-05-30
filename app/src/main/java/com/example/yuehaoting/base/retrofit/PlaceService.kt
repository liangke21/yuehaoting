package com.example.yuehaoting.base.retrofit

import com.example.yuehaoting.searchfor.data.kugou.KuGou
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    @GET("getSearchTip?MusicTipCount=12")
    fun searchPlaces(@Query("keyword") keyword: String): Call<KuGou>

}