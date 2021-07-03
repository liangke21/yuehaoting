package com.example.yuehaoting.playInterface.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.playInterface.livedata.KuGouSongPhoto

/**
 * 作者: 天使
 * 时间: 2021/7/3 15:42
 * 描述:
 */
class PlayViewModel:ViewModel() {

    var singerIdLeLiveData=MutableLiveData<String>()

    val singerIdObservedData=Transformations.switchMap(singerIdLeLiveData){id->
        KuGouSongPhoto.setSingerPhoto(id)
    }


    fun singerId(id:String){
        singerIdLeLiveData.value=id
    }
}