package com.example.yuehaoting.musicpath.service

import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.data.kugousingle.SongLists.Companion.SONG_LIST
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 15:17
 * 描述: 播放列队
 */
class PlayQueue {
    var song=SONG_LIST

    private val _originalQueue=ArrayList<SongLists>()

    val originalQueue:List<SongLists>
    get() = _originalQueue

    /**
     * 初始化列队
     */
    fun setPlayQueue(song:List<SongLists>){
        _originalQueue.clear()
        _originalQueue.addAll(song)
    }

    /**
     * 播放位置
     */
    fun setPosition(position: Int) {
      song=_originalQueue[position]
        Timber.d("后台播放7")
    }
}