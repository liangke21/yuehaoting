package com.example.yuehaoting.data.kugousingle

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 20:00
 * 描述:
 */
data class SongLists(var SongName:String,var SingerName:String,var FileHash:String){
    companion object{
        val SONG_LIST=SongLists("","","")
    }

}