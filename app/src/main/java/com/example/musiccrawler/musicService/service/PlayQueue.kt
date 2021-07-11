package com.example.musiccrawler.musicService.service

import com.example.musiccrawler.data.kugousingle.SongLists
import com.example.musiccrawler.data.kugousingle.SongLists.Companion.SONG_LIST
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 15:17
 * 描述: 播放列队
 */
class PlayQueue {
    /**
     * 当前歌曲属性
     */
    var song = SONG_LIST

    /**
     * 下一首歌
     */
    var nextSong = SONG_LIST

    /**
     * 当前播放的位置
     */
    private var position = 0

    /**
     * 下一首播放位置
     */
    private var nextPosition = 0


    private val _originalQueue = ArrayList<SongLists>()

    val originalQueue: List<SongLists>
        get() = _originalQueue

    /**
     * 初始化列队
     */
    fun setPlayQueue(song: List<SongLists>) {
        _originalQueue.clear()
        _originalQueue.addAll(song)
    }

    /**
     * 播放位置
     */
    fun setPosition(pos: Int) {
        position = pos
        song = _originalQueue[pos]
        Timber.d("后台播放7")
    }

    /**
     * 所有歌曲
     */
    fun size(): Int {
        return _originalQueue.size
    }

    /**
     * 播放下一首
     */
    fun next() {
        Timber.v("播放下一首6: %s")
        position = nextPosition
        song = nextSong.copy()
        updateNextSong()
    }

    /**
     * 播放上一首
     */
    fun previous(){
        Timber.v("position 指针位置: %s",position)
        if (--position<0){
            position=_originalQueue.size-1
        }
        Timber.v("position 指针位置: %s",position)
        if (position==-1 || position>_originalQueue.size-1){
            return
        }
        song=_originalQueue[position]
        Timber.v("播放上一首6: %s","position$position",)
        updateNextSong()
    }
     fun updateNextSong() {
        if (_originalQueue.isEmpty()){
            return
        }

        synchronized(this){
            nextPosition=position+1
            if (nextPosition>=_originalQueue.size){
                nextPosition=0
            }
            nextSong=_originalQueue[nextPosition]
        }

        Timber.v("播放下一首||播放上一首7: updateNextSong, curPos: $position nextPos: $nextPosition nextSong=${nextSong.SongName}\n" +
                " }")
    }
}