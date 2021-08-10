package com.example.yuehaoting.searchFor.livedata

import android.util.Log
import androidx.lifecycle.liveData
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.data.kugou.RecordData
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.data.musicKuWo.KuWoList
import com.example.yuehaoting.data.musicMiGu.MiGuList
import com.example.yuehaoting.data.musicQQ.QQSongList

import kotlinx.coroutines.Dispatchers
import timber.log.Timber

/**
 * 仓库层
 */
object Repository {
    private lateinit var HintInfo: List<RecordData>

    /**
     * 酷狗关键字
     */
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SongNetwork.searchPlaces(query)
            Log.d(placeResponse.status.toString(), "关键字请求-----------")
            if (placeResponse.status == 1) {
                val places = placeResponse.data[0]
                HintInfo = places.RecordDatas
                Result.success(HintInfo)
            } else {
                Result.failure(RuntimeException("为响应 ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<RecordData>(e)
        }
        emit(result)
    }

    /**
     * 酷狗搜索
     */
    fun singlePlaces(pages: Int, count: Int, name: String) = liveData(Dispatchers.IO) {
        val result:Result<KuGouSingle.Data> = try {
            val singleResponse = SongNetwork.singlePlaces(pages, count, name)
            Timber.e("曲目请求成功1-------------------- :%s", singleResponse.status)



            run{
             if (singleResponse.status == 1) {

                    val data = singleResponse.data
                    val list = data.lists
                    Timber.e("酷狗音乐列表请求成功-------------------- :%s", list[0].SongName)

                } else {
                 Timber.e("酷狗音乐列表请求失败-------------------- :%s", singleResponse.status)
                }
                Result.success(singleResponse.data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
        emit(result)
    }

    /**
     * 网易音乐列表
     */
    fun music163(input: String, filter: String, type: String, page: Int) = liveData(Dispatchers.IO) {
        val result: Result<MusicData> = try {
            val songList = SongNetwork.songList(input, filter, type, page)
            run {
                if (songList.code == 200) {
                    Timber.v("网易音乐列表请求成功:%s %s %s", songList.code, songList.data[0].title, songList.data[0].author)

                } else {
                    Timber.e("网易音乐列表请求失败:%s", songList.code)

                }
                Result.success(songList)
            }
        } catch (e: Exception) {

            Result.failure(e)
        }
        emit(result)
    }

    /**
     * qq音乐列表
     */
    fun musicQQ(p: Int, n: Int, w: String) = liveData(Dispatchers.IO) {
        val result: Result<QQSongList> = try {
            val songList = SongNetwork.qqSongList(p, n, w)
            run {
                if (songList.subcode == 0) {
                    Timber.v("QQ音乐列表请求成功:%s %s", songList.subcode, songList.data?.keyword)

                } else {
                    Timber.v("QQ音乐列表请求失败:%s", songList.subcode)
                }
                Result.success(songList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
        emit(result)
    }

    /**
     * 酷我音乐
     */
    fun musicKuWo(pages: Int, count: Int, name: String) = liveData(Dispatchers.IO) {
        val result: Result<KuWoList> = try {
            val songList = SongNetwork.kuWoList(pages, count, name)

            run {
                if (songList.isEmpty()) {
                    Timber.v("酷我音乐列表请求失败")
                } else {
                    Timber.v("酷我音乐请求成功:%s", songList)
                }
                Result.success(songList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
        emit(result)
    }

    /**
     * 咪咕音乐
     */
    fun musicMiGu(pages: Int, count: Int, name: String) = liveData(Dispatchers.IO) {
        val result: Result<MiGuList> = try {
            val songList = SongNetwork.miGuList(pages, count, name)
            run {
                if (songList.isEmpty()) {
                    Timber.v("酷我音乐列表请求失败")
                } else {
                    Timber.v("酷我音乐请求成功:%s", songList)
                }
                Result.success(songList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
        emit(result)
    }
}

