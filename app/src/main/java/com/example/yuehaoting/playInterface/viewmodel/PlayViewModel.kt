package com.example.yuehaoting.playInterface.viewmodel

import androidx.lifecycle.*
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhoto
import com.example.yuehaoting.playInterface.livedata.KuGouSongPhoto

/**
 * 作者: 天使
 * 时间: 2021/7/3 15:42
 * 描述:
 */
class PlayViewModel:ViewModel() {

    private var singerIdLeLiveData=MutableLiveData<String>()

       val singerPhotoList=ArrayList<SingerPhoto.Data.Imgs.Data4>()

    val singerIdObservedData: LiveData<Result<List<SingerPhoto.Data.Imgs.Data4>>> = Transformations.switchMap(singerIdLeLiveData){ id->
        KuGouSongPhoto.setSingerPhoto(id)
    }



    fun singerId(id:String){
        singerIdLeLiveData.value=id
    }
}