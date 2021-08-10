package com.example.yuehaoting.searchFor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.searchFor.livedata.Repository


/**
 * 作者: 天使
 * 时间: 2021/8/2 9:41
 * 描述:
 */
class SingleFragment3ViewModel : ViewModel() {

    private val parameter = MutableLiveData<Map<*, *>>()

    val observedData: LiveData<Result<MusicData>> = Transformations.switchMap(parameter) { p ->
        Repository.music163(p["key1"] as String, p["key2"] as String, p["key3"] as String, p["key4"] as Int)
    }

    /**
     * 网易音乐请求参数
     */
    fun requestParameter(input: String, filter: String, type: String, page: Int) {

        val numbersMap = mapOf("key1" to input, "key2" to filter, "key3" to type, "key4" to page)
        parameter.value = numbersMap
    }

    val songList = ArrayList<MusicData.Data>()
}