package com.example.yuehaoting.data.kugousingle

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 20:00
 * 描述:
 */
@Parcelize
data class SongLists(var SongName:String,var SingerName:String,var FileHash:String) : Parcelable {
    companion object {
        val SONG_LIST = SongLists("", "", "")
    }

}