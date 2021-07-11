package com.example.musiccrawler.musicService.data

import com.example.musiccrawler.base.retrofit.SongNetwork.songUriID
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/12 10:53
 * 描述:
 */
class KuGouSongMp3 {


    suspend fun songIDMp3(fileHash: String): String {

        val uriID = songUriID(fileHash, "")
        val id = uriID.data.album_id
        if (id == "0") {
            Timber.v("获取id失败 ")
            val mp3 = uriID.data.play_url
            Timber.v("获取mpe成功 : %s", mp3)

            return mp3
        }
        Timber.v("获取id成功 : %s", id)
        val uriMp3 = songUriID(fileHash, id)
        val mp3 = uriMp3.data.play_url
        Timber.i("获取mpe成功 : %s", mp3)
        return mp3

    }
}