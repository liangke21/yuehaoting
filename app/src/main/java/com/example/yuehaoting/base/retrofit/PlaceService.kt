package com.example.yuehaoting.base.retrofit

import com.example.yuehaoting.searchfor.data.kugou.KuGou
import com.example.yuehaoting.searchfor.data.kugousingle.KuGouSingle
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    @GET("getSearchTip?MusicTipCount=12")
    fun searchPlaces(@Query("keyword") keyword: String): Call<KuGou>

    @GET("song_search_v2?page=1&pagesize=30&userid=-1&%20clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0&_=1490845878887")
    fun singlePlaces(@Query("keyword") keyword: String): Call<KuGouSingle>

}