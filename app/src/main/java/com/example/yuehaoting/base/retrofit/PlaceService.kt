package com.example.yuehaoting.base.retrofit

import com.example.yuehaoting.data.kugou.KuGou
import com.example.yuehaoting.data.kugou.NewSong
import com.example.yuehaoting.data.kugou.specialRecommend.SetSpecialRecommend
import com.example.yuehaoting.data.kugou.specialRecommend.SpecialRecommend
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhotoData
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.kugousonguri.KuGouSongUriID
import com.example.yuehaoting.data.music163.Music163Search
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.data.musicKuWo.KuWoList
import com.example.yuehaoting.data.musicMiGu.MiGuList
import com.example.yuehaoting.data.musicMiGu.MiGuSearch
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
    fun singlePlaces(@Query("page") page: Int, @Query("pagesize") pagesize: Int, @Query("keyword") keyword: String): Call<KuGouSingle>

    //歌曲详情id请求
    @GET("yy/index.php?r=play/getdata&mid=aa2cdd4c0ed76a1623ac0b79f8d132c9")
    fun songUriId(@Query("hash") hash: String, @Query("album_id") album_id: String): Call<KuGouSongUriID>

    @GET("v1/author_image/audio?")
    fun singerPhoto(@Query("data") data: String): Call<SingerPhotoData>

    //手机酷狗主页特别推荐
    @POST("specialrec.service/special_recommend")
    fun kuGouSpecialRecommend(@Body ssr: SetSpecialRecommend): Call<SpecialRecommend>

    //酷狗新歌推荐
    @GET("api/v3/rank/newsong?version=9108&plat=0&with_cover=1&type=1&area_code=1")
    fun kuGouNewSongPlaceService(@Query("page") page: Int, @Query("pagesize") pagesize: Int): Call<NewSong>

    @GET("/{thread}.htm")
    fun hifIni(@Path("thread") thread: String): Call<ResponseBody>

    //网易音乐列表  废弃
    @Headers("x-requested-with: XMLHttpRequest")
    @FormUrlEncoded
    @POST("/")
    fun music1631(@Field("input") input: String, @Field("filter") filter: String, @Field("type") type: String, @Field("page") page: Int): Call<MusicData>

    @GET("api/web?_=1634719103613&types=search&source=netease")
    fun music1631(@Query("count") count: String, @Query("pages") pages: String, @Query("name") name: String): Call<Music163Search>

    //qq音乐搜索接口
    @GET("soso/fcgi-bin/client_search_cp?new_json=1&aggr=1&catZhida=1&format=json")
    fun musicQQ(@Query("p") p: Int, @Query("n") n: Int, @Query("w") w: String): Call<QQSongList>

    //酷我音乐接口
    @GET("api.php?source=kuwo&types=search")
    fun musicKuWo(@Query("pages") pages: Int, @Query("count") count: Int, @Query("name") name: String): Call<KuWoList>

    //酷我mp3
    @GET("api.php?types=url&source=kuwo")
    fun musicKuWoMp3(@Query("id") id: String): Call<ResponseBody>

    @GET("http://ali.asdi998.com:880/api.php?types=pic&source=kuwo")
    fun musicKuWoPic(@Query("id") id: String): Call<ResponseBody>

    //咪咕音乐搜索接口
    @GET("api/web?types=search&source=migu")
    fun musicMiGu(@Query("pages") pages: Int, @Query("count") count: Int, @Query("name") name: String): Call<MiGuList>
    @GET("v1/migu/search?")
    fun musicMiGuSearch(@Query("offset")offset:Int,@Query("limit")limit:Int,@Query("key")key:String):Call<MiGuSearch>

}