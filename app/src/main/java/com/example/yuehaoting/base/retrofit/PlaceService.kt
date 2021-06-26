package com.example.yuehaoting.base.retrofit

import com.example.yuehaoting.data.kugou.KuGou
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.kugousonguri.KuGouSongUriID
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    //搜索关键字请求
    @GET("getSearchTip?MusicTipCount=12")
    fun searchPlaces(@Query("keyword") keyword: String): Call<KuGou>

    //歌曲请求
    @GET("song_search_v2?page=1&pagesize=30&userid=-1&%20clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0&_=1490845878887")
    fun singlePlaces(@Query("keyword") keyword: String): Call<KuGouSingle>
    //去详情id请求
    @GET("yy/index.php?r=play/getdata&mid=aa2cdd4c0ed76a1623ac0b79f8d132c9")
   fun songUriId(@Query("hash")hash:String,@Query("album_id")album_id:String) :Call<KuGouSongUriID>


}