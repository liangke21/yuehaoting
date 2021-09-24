package com.example.yuehaoting.base.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 作者: LiangKe
 * 时间: 2021/9/23 8:43
 * 描述:
 */
@Entity(indices = [Index(value = ["audioId"], unique = true)])
class PlayQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val audioId: Long,
    var SongName:String,
    var SingerName:String,
    var FileHash:String,
    var mixSongID: String,
    var lyrics:String,
    val album:String,
    val pic:String,
    var platform:Int
){
    companion object{
        const val TABLE_MAME="PlayQueue"
    }

}