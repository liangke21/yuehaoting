package com.example.yuehaoting.base.db

import android.util.Log
import com.example.yuehaoting.App
import com.example.yuehaoting.base.db.model.PlayQueue
import com.example.yuehaoting.base.log.LogT.lll
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.Tag
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import java.util.concurrent.Callable
import kotlin.collections.ArrayList

/**
 * 作者: LiangKe
 * 时间: 2021/9/23 9:01
 * 描述:
 */
 class DatabaseRepository {
    private val db = AppDataBase.getInstance(App.context.applicationContext)

    /**
     * 获取播放队列
     */
    private fun getPlayQueue(): Single<List<SongLists>> {

        return Single
            .fromCallable {
        db.playQueueDao().selectAll()
                    .map {
                        SongLists(
                            it.audioId,
                            it.SongName,
                            it.SingerName,
                            it.FileHash,
                            it.mixSongID,
                            it.lyrics,
                            it.album,
                            it.pic,
                            it.platform
                        )

                    }

            }
    }

    /**
     * 插入多首歌曲到播放队列
     */
    fun insertToPlayQueue(audioIds: List<SongLists>): Single<Int> {

        val actual = audioIds.toMutableList()
        return getPlayQueue()
            .map {
                //不重复添加
                actual.removeAll(it)
                Timber.tag(Tag.queueDatabase).v("插入的数据  id %s %s", actual.toString(), lll())

                db.playQueueDao().insertPlayQueue(convertAudioIdsToPlayQueues(actual))

                actual.size
            }
    }

    /**
     * id 转换为 列表
     * @param audioIds List<Long>
     * @return List<PlayQueue>
     */
    private fun convertAudioIdsToPlayQueues(songLists: List<SongLists>): List<PlayQueue> {

        val playQueues = ArrayList<PlayQueue>()
        for (it in songLists) {
            playQueues.add(PlayQueue(0, it.id,it.SongName,it.SingerName,it.FileHash,it.mixSongID,it.lyrics,it.album,it.pic,it.platform))
        }
        return playQueues

    }

    /**
     * 获得播放队列对应的歌曲
     */

    fun getPlayQueueSongs(): Single<List<SongLists>> {
        val idsInQueue = ArrayList<PlayQueue>()

        return Single.fromCallable {
            Timber.tag(Tag.queueDatabase).v("获取本地数据库数据 fromCallable %s ", lll())
            db.playQueueDao().selectAll().map {
              /*  SongLists(
                    it.audioId,
                    it.SongName,
                    it.SingerName,
                    it.FileHash,
                    it.mixSongID,
                    it.lyrics,
                    it.album,
                    it.pic,
                    it.platform
                )*/
                it
            }
        }
            .doOnSuccess {
                Timber.tag(Tag.queueDatabase).v("获取本地数据库数据 doOnSuccess %s %s", it, lll())
                idsInQueue.addAll(it)
            }
            .flatMap {
                Timber.tag(Tag.queueDatabase).v("获取本地数据库数据 flatMap %s %s", it, lll())
                getSongsWithSort(it)
            }
            .doOnSuccess {
                Timber.tag(Tag.queueDatabase).v("获取本地数据库数据 flatMap %s %s", it, lll())

                }
            }


    /**
     * 从播放队列移除
     */
    fun deleteFromPlayQueue(audioIds: List<Long>): Single<Int> {
        return Single
            .fromCallable {
                deleteFromPlayQueueInternal(audioIds)
            }
    }

    /**
     * 从播放列队删除
     * @param audioIds List<Long>
     * @return Int
     */
    private fun deleteFromPlayQueueInternal(audioIds: List<Long>): Int {
        if (audioIds.isEmpty()) {
            return 0
        }
        return db.runInTransaction(Callable {
            var count = 0
            val length = audioIds.size / MAX_ARGUMENT_COUNT + 1
            for (i in 0 until length) {
                val lastIndex = if ((i + 1) * MAX_ARGUMENT_COUNT < audioIds.size) (i + 1) * MAX_ARGUMENT_COUNT else 1
                try {
                    count += db.playQueueDao().deleteSongs(audioIds.subList(i * MAX_ARGUMENT_COUNT, lastIndex))
                } catch (e: Exception) {
                    Timber.e(e)
                    CrashReport.postCatchedException(e)
                }
            }
            Timber.v("deleteFromPlayQueueInternal, count: $count")
            return@Callable count
        })
    }

    /**
     * 清空播放队列
     */
    fun clearPlayQueue(): Single<Int> {
        return Single
            .fromCallable {
                db.playQueueDao().clear()
            }
    }

    /**
     * 获取通过排序的歌曲
     * @param sort String
     * @param ids List<Long>
     * @return Single<List<SongLists>>
     */
    private fun getSongsWithSort(ids: List<PlayQueue>): Single<List<SongLists>> {
        val list = ArrayList<SongLists>()
        return Single
            .fromCallable {

                ids.map {
                    SongLists(
                        it.audioId,
                        it.SongName,
                        it.SingerName,
                        it.FileHash,
                        it.mixSongID,
                        it.lyrics,
                        it.album,
                        it.pic,
                        it.platform
                    )
                }
            }
    }


    companion object {

        @Volatile
        private var INSTANCE: DatabaseRepository? = null

        @JvmStatic
        fun getInstance(): DatabaseRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseRepository()
            }

        private const val MAX_ARGUMENT_COUNT = 300
    }


}
