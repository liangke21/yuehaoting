package com.example.yuehaoting.musicService.service

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.MusicConstant.PLAY_DATA_CHANGES
import com.example.yuehaoting.util.MusicConstant.PLAY_STATE_CHANGE
import com.example.yuehaoting.util.MusicConstant.UPDATE_META_DATA
import com.example.yuehaoting.util.MusicConstant.UPDATE_PLAY_STATE
import java.lang.ref.WeakReference


/**
 * 作者: 天使
 * 时间: 2021/7/9 21:42
 * 描述:
 */
class MusicServiceHandler(service: MusicService, private val musicServiceHandlerData: MusicServiceHandlerData): Handler(Looper.getMainLooper()) {

    //private val broadcastUtil=BroadcastUtil()
    private val wrf:WeakReference<MusicService> = WeakReference(service)
    override fun handleMessage(msg: Message) {
        if (wrf.get() == null){
            return
        }
     //   val musicService =wrf.get() ?:return
        when(msg.what){
            UPDATE_PLAY_STATE->handlePlayStateChange()

            UPDATE_META_DATA-> updatePlaybackData()
        }

    }

    /**
     * 处理播放状态
     */
  private fun handlePlayStateChange(){
      if (musicServiceHandlerData.playQueueSong== SongLists.SONG_LIST){
          return
      }

        BroadcastUtil.sendLocalBroadcast(Intent(PLAY_STATE_CHANGE))
  }


    interface MusicServiceHandlerData {
       val playQueueSong:SongLists
    }


    private fun updatePlaybackData(){
        BroadcastUtil.sendLocalBroadcast(Intent(PLAY_DATA_CHANGES))
    }
}

