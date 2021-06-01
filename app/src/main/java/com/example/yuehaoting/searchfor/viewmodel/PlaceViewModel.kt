package com.example.yuehaoting.searchfor.viewmodel

import androidx.lifecycle.*
import com.example.yuehaoting.searchfor.livedata.Repository
import com.example.yuehaoting.searchfor.data.kugou.RecordData

class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<RecordData>()

    //Transformations.switchMap 观察这个对象
    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->

        //发起网络请求
        Repository.searchPlaces(query) //返回LiveData对象
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

  fun SinglePlaces(single: String){
      println("-----$single----------------------------------------------------------------------------------------------")
  }
}