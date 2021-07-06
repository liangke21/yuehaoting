package com.example.yuehaoting.searchFor.livedata

import android.util.Log
import com.example.yuehaoting.base.retrofit.SongNetwork
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Test

/**
 * 作者: 天使
 * 时间: 2021/7/6 22:36
 * 描述:
 */
class RepositoryTest {

    @Test
    fun singlePlaces() {
        runBlocking {
            launch {
                val singleResponse = SongNetwork.singlePlaces("哇咔咔")
                println(singleResponse.status)
            }
        }
    }
}