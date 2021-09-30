package com.example.yuehaoting.base.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 作者: LiangKe
 * 时间: 2021/9/30 9:34
 * 描述:
 */

@Entity
class HistoryQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
) {
    companion object{

       const val HISTORY_QUEUE="HistoryQueue"
    }

}