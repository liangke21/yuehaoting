package com.example.yuehaoting.util

import android.content.Context
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.kotlin.setSp
import com.example.yuehaoting.util.MusicConstant.INITIAL_APP
import com.example.yuehaoting.util.MusicConstant.NAME

/**
 * 作者: LiangKe
 * 时间: 2021/11/3 13:41
 * 描述:
 */
object AppInitialization {
    /**
     * app初始化第一次要加载的数据
     * @return List<SongLists>
     */
    fun initializeTheCurrentSong(): List<SongLists> {
        val list = ArrayList<SongLists>()
        list.add(
            SongLists(
                id = 0,
                SongName = "一路生花",
                SingerName = "温奕心",
                FileHash = "5F302EBD7FF308C0C7D9B62F87CB4142",
                mixSongID = "45305792",
                lyrics = "",
                album = "一路生花",
                pic = "",
                platform = 1
            )
        )
        return list
    }
    /**
     * 第一次加载app
     */
    fun startTheAppForTheFirstTime(context: Context,back:()->Unit){
      val spB=  getSp(context,INITIAL_APP){
            getBoolean("CURRENT_SONG_App",true)
        }
         if (spB){
             back()
             setSp(context,INITIAL_APP){
                 putBoolean("CURRENT_SONG_App",false)
             }
         }
    }


}