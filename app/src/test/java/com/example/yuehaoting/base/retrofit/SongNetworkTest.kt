package com.example.yuehaoting.base.retrofit

import com.example.yuehaoting.kotlin.launchMy
import org.junit.Assert.*

import org.junit.Test
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/8/1 17:08
 * 描述:
 */
class SongNetworkTest {

    @Test
    fun songList() {
        launchMy {
            // val a=   SongNetwork.songList(p)
            val b=SongNetwork.songList("可不可以勇敢一点","name","netease",1)
            println(b)
        }
    }
}