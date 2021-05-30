package com.example.yuehaoting.searchfor.viewmodel

import androidx.lifecycle.*
import com.example.yuehaoting.searchfor.livedata.Repository
import com.example.yuehaoting.searchfor.data.kugou.RecordData

class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<RecordData>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }



}