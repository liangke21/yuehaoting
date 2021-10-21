package com.example.yuehaoting.searchFor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.data.musicKuWo.KuWoList
import com.example.yuehaoting.data.musicMiGu.MiGuList
import com.example.yuehaoting.data.musicMiGu.MiGuSearch
import com.example.yuehaoting.searchFor.livedata.Repository

/**
 * 作者: 天使
 * 时间: 2021/8/10 12:08
 * 描述:
 */
class SingleFragment6ViewModel : ViewModel() {

    private val parameter = MutableLiveData<Map<*, *>>()
    @Deprecated("废弃接口不好用", ReplaceWith("observedDataSearch"),level = DeprecationLevel.WARNING)
    val observedData: LiveData<Result<MiGuList>> = Transformations.switchMap(parameter) { p ->
        Repository.musicMiGu(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }
    val observedDataSearch: LiveData<Result<MiGuSearch>> = Transformations.switchMap(parameter) { p ->
        Repository.musicMiGuSearch(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }    /**
     * 咪咕音乐请求参数
     */
    fun requestParameter(p:Int,n:Int,w:String) {

        val numbersMap = mapOf("key1" to p, "key2" to n, "key3" to w)
        parameter.value = numbersMap
    }
    @Deprecated("废弃",ReplaceWith("songListSearch"))
    val songList = ArrayList<MiGuList.MiGuListItem>()

    val songListSearch = ArrayList<MiGuSearch.Music>()

}