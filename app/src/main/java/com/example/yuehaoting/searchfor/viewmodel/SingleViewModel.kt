package com.example.yuehaoting.searchfor.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.searchfor.data.kugousingle.KuGouSingle
import com.example.yuehaoting.searchfor.livedata.Repository

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 20:16
 * 描述:
 */
class SingleViewModel : ViewModel() {
    private var tAG = "PlaceViewModel层 曲目请求"
    private val singleLiveData = MutableLiveData<String>()

      var singleList=ArrayList<KuGouSingle.Data.Lists>()


    val singleObservedLiveData = Transformations.switchMap(singleLiveData) { query ->
        Repository.singlePlaces(query)
    }

    fun singlePlaces(single: String) {
        singleLiveData.value = single
        Log.e(tAG, single)
    }

}