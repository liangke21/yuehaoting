package com.example.yuehaoting.musicpath.data

import com.example.yuehaoting.base.retrofit.SongNetwork.songUriID

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/12 10:53
 * 描述:
 */
class KuGouSong {


    suspend fun songID(fileHash: String) {

        val uriID = songUriID(fileHash)
        var id = uriID.data.album_id
    }
}