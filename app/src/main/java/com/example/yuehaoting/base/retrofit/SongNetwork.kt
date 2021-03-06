package com.example.yuehaoting.base.retrofit

import com.example.yuehaoting.base.DataUri
import com.example.yuehaoting.data.kugou.specialRecommend.SetSpecialRecommend
import retrofit2.http.Query

object SongNetwork {
     //关键字请求
    private val placeService = ServiceCreator(DataUri.kuGouApiSearchFor).create(PlaceService::class.java)
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()
   //曲目请求
    private val singleService = ServiceCreator(DataUri.kuGouApiSingle).create(PlaceService::class.java)
    suspend fun singlePlaces(pages:Int,count:Int,name:String) = singleService.singlePlaces(pages,count,name).await()

    //歌曲id请求
    private val songUriID=ServiceCreator(DataUri.kuGouSongUriID).create(PlaceService::class.java)
    suspend fun songUriID(query: String,album:String)= songUriID.songUriId(query,album).await()

    //歌手写真
    private val singerPhoto=ServiceCreator(DataUri.kuGouSongPhoto).create(PlaceService::class.java)
    /**
     * 歌手写真数据
     */
    suspend fun singerPhoto(query: String)= singerPhoto.singerPhoto(query).await()
    //酷狗歌单特别推荐
     private val kuGouSpecialRecommend=ServiceCreator(DataUri.kuGouSpecialRecommend).create<PlaceService>()
     suspend fun kuGouSpecialRecommend(ssr: SetSpecialRecommend)= kuGouSpecialRecommend.kuGouSpecialRecommend(ssr).await()
    //酷狗新歌推荐

    private val kuGouNewSong = ServiceCreator(DataUri.kuGouNewSong).create<PlaceService> ()
    suspend fun kuGouNewSongSongNetwork(page:Int, size:Int)= kuGouNewSong.kuGouNewSongPlaceService(page,size).await()

//_______________________________________|Html|______________________________________________________________________________________________________
    private val hifIni=ServiceCreator(DataUri.HifIni).createHtml(PlaceService::class.java)

    suspend fun hifIniT(thread:String)= hifIni.hifIni(thread).awaitHtml()

    //网易音乐列表 废弃
    private val songLists=ServiceCreator(DataUri.music163).create(PlaceService::class.java)
    suspend fun songList(input:String,filter:String, type:String, page:Int)=songLists.music1631(input, filter, type, page).await()

    private val songList163=ServiceCreator(DataUri.music163).create<PlaceService>()
    suspend fun songLists163(count:String,pages:String,name: String) = songList163.music1631(count, pages, name).await()

    //QQ搜索
    private val musicQQ=ServiceCreator(DataUri.musicQQ).create<PlaceService>()
    suspend fun qqSongList(p:Int,n:Int,w:String)= musicQQ.musicQQ(p, n, w).await()

    //酷我音乐搜索
    private val musicKuWo=ServiceCreator(DataUri.musicKuWo).create<PlaceService>()
    suspend fun kuWoList(pages:Int,count:Int,name:String)= musicKuWo.musicKuWo(pages,count,  name).await()

    //酷我音乐mp3

    private val musicKuWoMp3=ServiceCreator(DataUri.musicKuWo).create<PlaceService>()
    suspend fun musicKuWoMp3(id: String)=musicKuWoMp3.musicKuWoMp3(id).await()
    //酷我音乐pic
    private val musicKuWoPic=ServiceCreator(DataUri.musicKuWo).create<PlaceService>()
    suspend fun musicKuWoPic(id: String)=musicKuWoPic.musicKuWoPic(id).await()

    //咪咕音乐列表
    private val musicMiGu=ServiceCreator(DataUri.musicMiGu).create<PlaceService>()
    suspend fun miGuList(pages:Int,count:Int,name:String)= musicMiGu.musicMiGu(pages, count, name).await()
    //咪咕音乐列表
    private val musicMiGuSearch=ServiceCreator(DataUri.musicMiGuSearch).create<PlaceService>()
    suspend fun miGuListSearch(pages:Int,count:Int,name:String)= musicMiGuSearch.musicMiGuSearch(pages, count, name).await()
}