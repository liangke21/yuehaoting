package com.example.yuehaoting.base.retrofit

import com.example.yuehaoting.data.kugou.KuGou
import com.example.yuehaoting.data.kugou.specialRecommend.SetSpecialRecommend
import com.example.yuehaoting.data.kugou.specialRecommend.SpecialRecommend
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhoto
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.kugousonguri.KuGouSongUriID
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.data.musicKuWo.KuWoList
import com.example.yuehaoting.data.musicMiGu.MiGuList
import com.example.yuehaoting.data.musicQQ.QQSongList
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface PlaceService {

    //搜索关键字请求
    @GET("getSearchTip?MusicTipCount=12")
    fun searchPlaces(@Query("keyword") keyword: String): Call<KuGou>

    //歌曲请求
    @GET("song_search_v2?userid=-1&%20clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0&_=1490845878887")
    fun singlePlaces(@Query("page")page:Int,@Query("pagesize")pagesize:Int,@Query("keyword") keyword: String): Call<KuGouSingle>
    //歌曲详情id请求
    @GET("yy/index.php?r=play/getdata&mid=aa2cdd4c0ed76a1623ac0b79f8d132c9")
   fun songUriId(@Query("hash")hash:String,@Query("album_id")album_id:String) :Call<KuGouSongUriID>
   @GET("v1/author_image/audio?")
   fun singerPhoto(@Query("data")data:String):Call<SingerPhoto>

   //手机酷狗主页特别推荐
   @POST("specialrec.service/special_recommend")
   fun kuGouSpecialRecommend(@Body ssr: SetSpecialRecommend):Call<SpecialRecommend>


   @GET("/{thread}.htm")
   fun hifIni(@Path("thread")thread:String):Call<ResponseBody>

   //网易音乐列表
    @Headers("x-requested-with: XMLHttpRequest")
    @FormUrlEncoded
    @POST("/")
    fun music1631(@Field("input")input:String,@Field("filter")filter:String,@Field("type")type:String,@Field("page")page:Int):Call<MusicData>

    //qq音乐搜索接口
    @GET("soso/fcgi-bin/client_search_cp?new_json=1&aggr=1&catZhida=1&format=json")
    fun musicQQ(@Query("p")p:Int,@Query("n")n:Int,@Query("w")w:String):Call<QQSongList>

    //酷我音乐接口
    @GET("api.php?source=kuwo&types=search")
    fun musicKuWo(@Query("pages")pages:Int,@Query("count")count:Int,@Query("name")name:String):Call<KuWoList>

    //咪咕音乐搜索接口
    @GET("api/web?types=search&source=migu")
    fun musicMiGu(@Query("pages")pages:Int,@Query("count")count:Int,@Query("name")name:String):Call<MiGuList>
}