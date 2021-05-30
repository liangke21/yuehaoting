package com.example.yuehaoting.searchfor.livedata

import android.util.Log
import androidx.lifecycle.liveData
import com.example.yuehaoting.base.retrofit.SunnyWeatherNetwork
import com.example.yuehaoting.searchfor.data.kugou.RecordData

import kotlinx.coroutines.Dispatchers


object Repository {
private lateinit var HintInfo:List< RecordData>
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            Log.e(placeResponse.status.toString(), "-----------")
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
}

