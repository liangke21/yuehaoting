package com.example.yuehaoting.searchFor.viewmodel

import androidx.lifecycle.*
import com.example.yuehaoting.searchFor.livedata.Repository
import com.example.yuehaoting.data.kugou.RecordData
import com.example.yuehaoting.data.kugousingle.KuGouSingle

class PlaceViewModel : ViewModel() {
private var tAG=PlaceViewModel::class.java.simpleName
    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<RecordData>()

    //Transformations.switchMap 观察这个对象
    val placeLiveData :LiveData<Result<List<RecordData>>> = Transformations.switchMap(searchLiveData) { query ->

        //发起网络请求
        Repository.searchPlaces(query) //返回LiveData对象
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    ////////////////////////////////热搜关键字 /////////////////////////////
    private val singleLiveData = MutableLiveData<Map<*, *>>()

    val singleObservedLiveData : LiveData<Result<KuGouSingle.Data>> = Transformations.switchMap(singleLiveData) { p ->
        Repository.singlePlaces(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }


    /**
     * 酷狗音乐请求参数
     */
    fun requestParameter(p:Int,n:Int,w:String) {

        val numbersMap = mapOf("key1" to p, "key2" to n, "key3" to w)
        singleLiveData.value = numbersMap
    }
    val songList=ArrayList<List<KuGouSingle.Data.Lists>>()

    val songList1 = ArrayList<KuGouSingle.Data.Lists>()
    val songList2 = ArrayList<KuGouSingle.Data.Lists>()
    val songList3 = ArrayList<KuGouSingle.Data.Lists>()
    val songList4 = ArrayList<KuGouSingle.Data.Lists>()
    val songList5 = ArrayList<KuGouSingle.Data.Lists>()


}