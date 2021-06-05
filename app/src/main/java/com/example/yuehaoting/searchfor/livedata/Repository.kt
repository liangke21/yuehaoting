package com.example.yuehaoting.searchfor.livedata

import android.util.Log
import androidx.lifecycle.liveData
import com.example.yuehaoting.base.log.log
import com.example.yuehaoting.base.retrofit.SunnyWeatherNetwork
import com.example.yuehaoting.searchfor.data.kugou.RecordData

import kotlinx.coroutines.Dispatchers

/**
 * 仓库层
 */
object Repository {
private lateinit var HintInfo:List< RecordData>
private var tAG:String="LiveData层"
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {

        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
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



 fun singlePlaces(query: String) =liveData(Dispatchers.IO) {
     val result = try {
         val singleResponse = SunnyWeatherNetwork.singlePlaces(query)
         if (singleResponse.status == 1) {
             val data = singleResponse.data
             val list=data.lists
             log.e(list[0].SongName,"曲目请求成功--------------------")
             Result.success(list)
         } else {
             Result.failure(RuntimeException("为响应 ${singleResponse.status}"))
         }
     } catch (e: Exception) {
         Result.failure<RecordData>(e)
     }
     emit(result)
 }


}

