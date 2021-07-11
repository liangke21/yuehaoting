package com.example.musiccrawler.data.kugousingle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 20:00
 * 描述:
 */
@Parcelize
data class SongLists(var SongName:String,var SingerName:String,var FileHash:String,var mixSongID: String) : Parcelable {
    companion object {
        val SONG_LIST = SongLists("", "", "","")
    }

}