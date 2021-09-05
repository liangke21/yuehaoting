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

    private val liveData = MutableLiveData<String>()

    val observedLiveData: LiveData<Result<NewSong>> = Transformations.switchMap(liveData) {

        Repository.kuGouNewSongRepository()
    }

    fun kuGouSpecialRecommendViewModel(string: String) {
        liveData.value=string
    }

    val listLiveData = ArrayList<NewSong.Data.Info>()




}