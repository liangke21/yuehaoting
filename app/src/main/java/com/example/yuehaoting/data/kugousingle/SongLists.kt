package com.example.yuehaoting.data.kugousingle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 20:00
 * 描述:
 * SongName: 歌曲名字
 * SingerName: 歌手
 * FileHash:歌曲嘻哈 或者 MP3连接 或者歌曲id
 * mixSongID:歌手id
 * lyrics:歌词
 * album:专辑
 * pic:封面
 * platform 表示歌曲平台 1表示酷狗,2表示HifIni
 */
@Parcelize
data class SongLists(
    val id: Long,
    var SongName:String,
    var SingerName:String,
    var FileHash:String,
    var mixSongID: String,
    var lyrics:String,
    val album:String,
    val pic:String,
    var platform:Int
    ) : Parcelable {




    companion object {
        val SONG_LIST = SongLists(id= -1L,"", "", "","","", "","",0)
    }

    override fun toString(): String {
        return "SongLists(id=$id, SongName='$SongName', SingerName='$SingerName', FileHash='$FileHash', mixSongID='$mixSongID', lyrics='$lyrics', album='$album', pic='$pic', platform=$platform)"
    }

}