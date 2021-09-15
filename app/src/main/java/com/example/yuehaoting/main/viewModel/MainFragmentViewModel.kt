package com.example.yuehaoting.main.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.data.kugou.specialRecommend.SetSpecialRecommend
import com.example.yuehaoting.data.kugou.specialRecommend.SpecialRecommend
import com.example.yuehaoting.main.livedata.Repository.kuGouSpecialRecommend

/**
 * 作者: 天使
 * 时间: 2021/8/29 14:49
 * 描述:
 */
class MainFragmentViewModel:ViewModel() {

 private   val liveData = MutableLiveData<SetSpecialRecommend> ()

    val observedLiveData = Transformations.switchMap(liveData){

        kuGouSpecialRecommend(it)
    }

    fun kuGouSpecialRecommendViewModel(ssr:SetSpecialRecommend){
        liveData.value=ssr
    }

    val listLiveData  = ArrayList<SpecialRecommend.Data.Special?>()
}