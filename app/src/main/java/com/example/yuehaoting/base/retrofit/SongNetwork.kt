package com.example.yuehaoting.base.retrofit

import com.example.yuehaoting.base.DataUri
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.data.music163.PostMusic
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field


import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object SongNetwork {
     //关键字请求
    private val placeService = ServiceCreator(DataUri.kuGouApiSearchFor).create(PlaceService::class.java)
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()
   //曲目请求
    private val singleService = ServiceCreator(DataUri.kuGouApiSingle).create(PlaceService::class.java)
    suspend fun singlePlaces(query: String) = singleService.singlePlaces(query).await()

    //歌曲id请求
    private val songUriID=ServiceCreator(DataUri.kuGouSongUriID).create(PlaceService::class.java)
    suspend fun songUriID(query: String,album:String)= songUriID.songUriId(query,album).await()

    //歌手写真
    private val singerPhoto=ServiceCreator(DataUri.kuGouSongPhoto).create(PlaceService::class.java)
    /**
     * 歌手写真数据
     */
    suspend fun singerPhoto(query: String)= singerPhoto.singerPhoto(query).await()

//_______________________________________|Html|______________________________________________________________________________________________________
    private val hifIni=ServiceCreator(DataUri.HifIni).createHtml(PlaceService::class.java)

    suspend fun hifIniT(thread:String)= hifIni.hifIni(thread).awaitHtml()

    //网易音乐列表
    private val songLists=ServiceCreator(DataUri.music163).create(PlaceService::class.java)
    suspend fun songList(input:String,filter:String, type:String, page:Int)=songLists.music1631(input, filter, type, page).await()

    //QQ搜索
    private val musicQQ=ServiceCreator(DataUri.musicQQ).create<PlaceService>()
    suspend fun qqSongList(p:Int,n:Int,w:String)= musicQQ.musicQQ(p, n, w).await()
}