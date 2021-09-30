package com.example.yuehaoting.base.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yuehaoting.base.db.model.HistoryQueue

/**
 * 作者: LiangKe
 * 时间: 2021/9/30 9:40
 * 描述:
 */
@Dao
interface HistoryDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertHistory(name:HistoryQueue):Long


    @Query("SELECT * FROM HistoryQueue")
    fun selectAll():List<HistoryQueue>


    /**
     * 清空
     * @return Int
     */
    @Query("DELETE FROM HistoryQueue")
    fun clear(): Int

    /**
     *  删除
     * @param audioIds List<Long>
     * @return Int
     */
    @Query(" DELETE FROM HistoryQueue WHERE name IN (:audioIds)")
    fun deleteSongs(audioIds: List<Long>): Int
}