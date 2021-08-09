package com.example.yuehaoting.searchFor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.data.musicQQ.QQSongList
import com.example.yuehaoting.searchFor.livedata.Repository

/**
 * 作者: 天使
 * 时间: 2021/8/9 16:33
 * 描述:
 */
class SingleFragment4ViewModel:ViewModel() {
    private val parameter = MutableLiveData<Map<*, *>>()

    val observedData: LiveData<Result<QQSongList>> = Transformations.switchMap(parameter) { p ->
        Repository.musicQQ(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }

    /**
     * qq音乐请求参数
     */
    fun requestParameter(p:Int,n:Int,w:String) {

        val numbersMap = mapOf("key1" to p, "key2" to n, "key3" to w)
        parameter.value = numbersMap
    }

    val songList = ArrayList<QQSongList.Data.Song.Lists>()

}