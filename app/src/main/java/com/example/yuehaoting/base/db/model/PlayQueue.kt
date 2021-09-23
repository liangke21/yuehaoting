package com.example.yuehaoting.base.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 作者: LiangKe
 * 时间: 2021/9/23 8:43
 * 描述:
 */
@Entity(indices = [Index(value = ["audio_id"], unique = true)])
class PlayQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val audio_id: Long
){
    companion object{
        const val TABLE_MAME="PlayQueue"
    }

}