package com.example.yuehaoting.callback

import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.musicService.service.MusicService

/**
 * 作者: 天使
 * 时间: 2021/6/25 10:57
 * 描述: 音乐事假回调
 */
interface MusicEvenCallback {
    //媒体商店已更改
    fun onMediaStoreChanged()
    //关于权限已经更改
    fun onPermissionChanged(has:Boolean)
    //播放列表已更改
    fun onPlayListChanged(name:String)
    //服务连接撒上
    fun  onServiceConnected(service:MusicService)
    //播放数据改遍
    fun onMetaChanged()
    //播放状态更改
    fun onPlayStateChange()
    //服务断开
    fun onServiceDisConnected()
    // 标签更改
    fun  onTagChanged(oldSong:SongLists,newSongLists: SongLists)



}