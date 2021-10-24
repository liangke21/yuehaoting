package com.example.yuehaoting.playInterface.viewmodel

import androidx.lifecycle.*
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhotoData
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.playInterface.livedata.KuGouSongPhoto
import com.example.yuehaoting.searchFor.livedata.Repository
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/7/3 15:42
 * 描述:
 */
class PlayViewModel:ViewModel() {

    private var singerIdLeLiveData=MutableLiveData<String>()

       val singerPhotoList=ArrayList<SingerPhotoData.Data.Imgs.Data4>()

    val singerIdObservedData: LiveData<Result<SingerPhotoData>>  = Transformations.switchMap(singerIdLeLiveData){ id ->
        Timber.v(" singerIdObservedData歌手写真ID %s",id)
        KuGouSongPhoto.setSingerPhoto(id)
    }



    fun singerId(id:String){
        singerIdLeLiveData.value=id
    }


    fun  cleared(){
        onCleared()
    }

}