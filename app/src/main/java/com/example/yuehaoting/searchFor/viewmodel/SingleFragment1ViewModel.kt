package com.example.yuehaoting.searchFor.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.data.musicMiGu.MiGuList
import com.example.yuehaoting.searchFor.livedata.Repository

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 20:16
 * 描述:
 */
class SingleFragment1ViewModel : ViewModel() {
    private var tAG = "PlaceViewModel层 曲目请求"
    private val singleLiveData = MutableLiveData<Map<*, *>>()

      var singleList=ArrayList<KuGouSingle.Data.Lists>()


    val singleObservedLiveData : LiveData<Result<KuGouSingle.Data>> = Transformations.switchMap(singleLiveData) { p ->
        Repository.singlePlaces(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }


    /**
     * qq音乐请求参数
     */
    fun requestParameter(p:Int,n:Int,w:String) {

        val numbersMap = mapOf("key1" to p, "key2" to n, "key3" to w)
        singleLiveData.value = numbersMap
    }

    val songList = ArrayList<KuGouSingle.Data.Lists>()

}