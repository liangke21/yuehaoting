package com.example.yuehaoting.searchFor.viewmodel

import androidx.lifecycle.ViewModel
import com.example.musiccrawler.hifini.DataSearch

/**
 * 作者: 天使
 * 时间: 2021/7/12 18:04
 * 描述:
 */
class SingleFragment2ViewModel:ViewModel() {




    var single:String?=null

    var singleList=ArrayList<DataSearch.Attributes>()




    fun singlePlaces(single:String) {
        this.single=single
    }



}