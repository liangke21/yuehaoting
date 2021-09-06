package com.example.yuehaoting.mian.fragment1.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.yuehaoting.data.kugou.NewSong
import com.example.yuehaoting.mian.fragment1.liveData.Repository

/**
 * 作者: 天使
 * 时间: 2021/8/29 14:49
 * 描述:
 */
class FragmentAKuGouViewModel : ViewModel() {

    private val liveData = MutableLiveData<List<Int>>()

    val observedLiveData: LiveData<Result<NewSong>> = Transformations.switchMap(liveData) {

        Repository.kuGouNewSongRepository(it[0], it[1])
    }

    fun kuGouSpecialRecommendViewModel(page:Int, size:Int) {
        val list= arrayListOf(page,size)
        liveData.value= list
    }

    val listLiveData = ArrayList<NewSong.Data.Info>()




}