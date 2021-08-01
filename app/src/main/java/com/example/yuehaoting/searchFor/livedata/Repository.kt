package com.example.yuehaoting.searchFor.livedata

import android.util.Log
import androidx.lifecycle.liveData
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.data.kugou.RecordData
import com.example.yuehaoting.data.music163.MusicData

import kotlinx.coroutines.Dispatchers
import timber.log.Timber

/**
 * 仓库层
 */
object Repository {
    private lateinit var HintInfo: List<RecordData>
    private var tAG: String = "LiveData层"
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


    fun singlePlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val singleResponse = SongNetwork.singlePlaces(query)
            Timber.e("曲目请求成功1-------------------- :%s", singleResponse.status)
            if (singleResponse.status == 1) {

                val data = singleResponse.data
                val list = data.lists
                Timber.e("曲目请求成功2-------------------- :%s", list[0].SongName)
                Result.success(list)
            } else {
                Result.failure(RuntimeException("为响应 ${singleResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<RecordData>(e)
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
                    Timber.v("网易音乐列表请求成功:%s", songList.code)

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
}

