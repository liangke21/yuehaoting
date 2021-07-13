package com.example.yuehaoting.searchFor.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.searchFor.livedata.Repository

/**
 * 作者: 天使
 * 时间: 2021/7/12 18:04
 * 描述:
 */
class SingleFragment2ViewModel:ViewModel() {



    private var tAG = "PlaceViewModel层 曲目请求"
    private val singleLiveData = MutableLiveData<String>()

   var single:String?=null

    var singleList=ArrayList<String>()


    val singleObservedLiveData = Transformations.switchMap(singleLiveData) { query ->
        Repository.singlePlaces(query)
    }

    fun singlePlaces(single:String) {
        singleLiveData.value=single
        this.single=single
    }



}