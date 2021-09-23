package com.example.yuehaoting.base.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yuehaoting.base.db.model.PlayQueue

/**
 * 作者: LiangKe
 * 时间: 2021/9/23 8:53
 * 描述:
 */
@Dao
interface PlayQueueDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertPlayQueue(playQueue: List<PlayQueue>):LongArray
    /**
     * 查询这个库
     * @return List<PlayQueue>
     */
    @Query("SELECT * FROM PlayQueue")
    fun selectAll():List<PlayQueue>

    /**
     * 清空
     * @return Int
     */
    @Query("DELETE FROM PlayQueue")
    fun clear(): Int

    /**
     *  删除歌曲
     * @param audioIds List<Long>
     * @return Int
     */
    @Query(" DELETE FROM PlayQueue WHERE audio_id IN (:audioIds)")
    fun deleteSongs(audioIds: List<Long>): Int
}