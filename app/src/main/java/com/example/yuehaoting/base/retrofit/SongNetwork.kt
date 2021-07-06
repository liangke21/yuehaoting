package com.example.yuehaoting.base.retrofit

import com.example.yuehaoting.base.DataUri
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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



    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()

                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("响应为空"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}