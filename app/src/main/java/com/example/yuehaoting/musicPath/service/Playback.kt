package com.example.yuehaoting.musicPath.service

import java.text.FieldPosition

/**
 * 作者: 天使
 * 时间: 2021/6/24 9:18
 * 描述:
 */
interface Playback {
    //播放选着歌曲
    fun playSelectSong(position: Int)

    fun toggle()

    fun playNext()

    fun playPrecious()

    fun play(fadeIn:Boolean)

    fun pause(updateMediaSessionOnly: Boolean)



}