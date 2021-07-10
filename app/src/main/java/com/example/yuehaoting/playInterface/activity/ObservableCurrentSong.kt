package com.example.yuehaoting.playInterface.activity

import com.example.yuehaoting.musicService.service.MusicServiceRemote
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * 作者: 天使
 * 时间: 2021/7/10 17:18
 * 描述:
 */
class ObservableCurrentSong {

    var nameCurrentSong: String by Delegates.observable(MusicServiceRemote.getCurrentSong().mixSongID) {
            prop, old, new ->
        Timber.v("nameCurrentSong:%s %s %s",prop, old, new)
       if (old!=new){
            //关闭封面幻影灯片
            SingerPhoto.handlerRemoveCallbacks()
        }
    }
}