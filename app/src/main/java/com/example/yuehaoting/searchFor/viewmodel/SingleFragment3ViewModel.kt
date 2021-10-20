package com.example.yuehaoting.searchFor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.data.music163.Music163Search
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.searchFor.livedata.Repository


/**
 * 作者: 天使
 * 时间: 2021/8/2 9:41
 * 描述:
 */
class SingleFragment3ViewModel : ViewModel() {

    private val parameter = MutableLiveData<Map<*, *>>()

    @Deprecated("请求参数发生变化,因为接口失效", ReplaceWith("observedData163"), level = DeprecationLevel.WARNING)
    val observedData: LiveData<Result<MusicData>> = Transformations.switchMap(parameter) { p ->
        Repository.music163(p["key1"] as String, p["key2"] as String, p["key3"] as String, p["key4"] as Int)
    }

    val observedData163: LiveData<Result<Music163Search>> = Transformations.switchMap(parameter) { p ->
        Repository.music163(p["key1"] as String, p["key2"] as String, p["key3"] as String)
    }

    /**
     * 网易音乐请求参数
     */
    @Deprecated("请求参数发生变化,因为接口失效", ReplaceWith("requestParameter(count, pages, name)", " requestParameter"), level = DeprecationLevel.WARNING)
    fun requestParameter(input: String, filter: String, type: String, page: Int) {

        val numbersMap = mapOf("key1" to input, "key2" to filter, "key3" to type, "key4" to page)
        parameter.value = numbersMap
    }

    fun requestParameter(count: String, pages: String, name: String) {
        val numbersMap = mapOf("key1" to count, "key2" to pages, "key3" to name)
        parameter.value = numbersMap
    }

    val songList = ArrayList<MusicData.Data>()
}