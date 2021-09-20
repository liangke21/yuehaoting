package com.example.yuehaoting.main.discover.fragment1.newSongRecommendationFragment

import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder

/**
 * 作者: LiangKe
 * 时间: 2021/9/5 22:46
 * 描述:
 */
interface ShowNewSongList {

    fun haveInternet()


    fun  noInternet()

    fun haveInternetShowNewSongList()

    fun noInternetShowNewSongList()

    fun songPlay(holder: SmartViewHolder?, position: Int)
}