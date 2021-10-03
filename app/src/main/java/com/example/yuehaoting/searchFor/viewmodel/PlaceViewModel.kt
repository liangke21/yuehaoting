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
    //_______________________________________|1|______________________________________________________________________________________________________
    private val singleLiveData1 = MutableLiveData<Map<*, *>>()

    val singleObservedLiveData1 : LiveData<Result<KuGouSingle.Data>> = Transformations.switchMap(singleLiveData1) { p ->
        Repository.singlePlaces(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }
    /**
     * 酷狗音乐请求参数
     */
    fun requestParameter1(p:Int,n:Int,w:String) {

        val numbersMap = mapOf("key1" to p, "key2" to n, "key3" to w)
        singleLiveData1.value = numbersMap
    }

//_______________________________________|2|______________________________________________________________________________________________________

    private val singleLiveData2 = MutableLiveData<Map<*, *>>()

    val singleObservedLiveData2 : LiveData<Result<KuGouSingle.Data>> = Transformations.switchMap(singleLiveData2) { p ->
        Repository.singlePlaces(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }
    /**
     * 酷狗音乐请求参数
     */
    fun requestParameter2(p:Int,n:Int,w:String) {

        val numbersMap = mapOf("key1" to p, "key2" to n, "key3" to w)
        singleLiveData2.value = numbersMap
    }

//_______________________________________|3|______________________________________________________________________________________________________

    private val singleLiveData3 = MutableLiveData<Map<*, *>>()

    val singleObservedLiveData3 : LiveData<Result<KuGouSingle.Data>> = Transformations.switchMap(singleLiveData3) { p ->
        Repository.singlePlaces(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }
    /**
     * 酷狗音乐请求参数
     */
    fun requestParameter3(p:Int,n:Int,w:String) {

        val numbersMap = mapOf("key1" to p, "key2" to n, "key3" to w)
        singleLiveData3.value = numbersMap
    }

//_______________________________________|4|______________________________________________________________________________________________________

    private val singleLiveData4 = MutableLiveData<Map<*, *>>()

    val singleObservedLiveData4 : LiveData<Result<KuGouSingle.Data>> = Transformations.switchMap(singleLiveData4) { p ->
        Repository.singlePlaces(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }
    /**
     * 酷狗音乐请求参数
     */
    fun requestParameter4(p:Int,n:Int,w:String) {

        val numbersMap = mapOf("key1" to p, "key2" to n, "key3" to w)
        singleLiveData4.value = numbersMap
    }
//_______________________________________|4|______________________________________________________________________________________________________
private val singleLiveData5 = MutableLiveData<Map<*, *>>()

    val singleObservedLiveData5 : LiveData<Result<KuGouSingle.Data>> = Transformations.switchMap(singleLiveData5) { p ->
        Repository.singlePlaces(p["key1"] as Int, p["key2"] as Int, p["key3"] as String)
    }
    /**
     * 酷狗音乐请求参数
     */
    fun requestParameter5(p:Int,n:Int,w:String) {

        val numbersMap = mapOf("key1" to p, "key2" to n, "key3" to w)
        singleLiveData5.value = numbersMap
    }





    val songList=ArrayList<List<KuGouSingle.Data.Lists>>()

    val songList1 = ArrayList<KuGouSingle.Data.Lists>()
    val songList2 = ArrayList<KuGouSingle.Data.Lists>()
    val songList3 = ArrayList<KuGouSingle.Data.Lists>()
    val songList4 = ArrayList<KuGouSingle.Data.Lists>()
    val songList5 = ArrayList<KuGouSingle.Data.Lists>()


}