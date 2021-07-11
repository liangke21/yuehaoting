package com.example.musiccrawler.searchFor.livedata

import com.example.musiccrawler.base.retrofit.SongNetwork
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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