package com.example.yuehaoting.base.db

import com.example.yuehaoting.App
import com.example.yuehaoting.base.db.model.PlayQueue
import com.example.yuehaoting.data.kugousingle.SongLists
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import java.util.concurrent.Callable

/**
 * 作者: LiangKe
 * 时间: 2021/9/23 9:01
 * 描述:
 */
class DatabaseRepository() {
  private val db=AppDataBase.getInstance(App.context.applicationContext)
  /**
   * 获取播放队列
   */
  fun getPlayQueue(): Single<List<Long>> {
    return Single
      .fromCallable {
        db.playQueueDao().selectAll()
          .map {
            it.audio_id
          }
      }
  }

  /**
   * 插入多首歌曲到播放队列
   */
  fun insertToPlayQueue(audioIds: List<Long>): Single<Int> {
    val actual = audioIds.toMutableList()
    return getPlayQueue()
      .map {
        //不重复添加
        actual.removeAll(it)

        db.playQueueDao().insertPlayQueue(convertAudioIdsToPlayQueues(actual))

        actual.size
      }
  }

  /**
   * id 转换为 列表
   * @param audioIds List<Long>
   * @return List<PlayQueue>
   */
  private fun convertAudioIdsToPlayQueues(audioIds: List<Long>): List<PlayQueue> {
    val playQueues = ArrayList<PlayQueue>()
    for (audioId in audioIds) {
      playQueues.add(PlayQueue(0, audioId))
    }
    return playQueues
  }

    /**
     * 获得播放队列对应的歌曲
     */

    fun getPlayQueueSongs(): Single<List<SongLists>> {
        val idsInQueue = ArrayList<Long>()

      return Single.fromCallable {
       db.playQueueDao().selectAll().map {
         it.audio_id
       }
      }
        .doOnSuccess{
          idsInQueue.addAll(it)
        }
        .flatMap {
          getSongsWithSort( it)
        }
        .doOnSuccess { songs ->
          //删除不存在的歌曲
          if (songs.size < idsInQueue.size) {
            Timber.v("删除播放队列中不存在的歌曲")
            val deleteIds = ArrayList<Long>()
            val existIds = songs.map { it.id }

            for (audioId in idsInQueue) {
              if (!existIds.contains(audioId)) {
                deleteIds.add(audioId)
              }
            }

            if (deleteIds.isNotEmpty()) {
              //deleteFromPlayQueueInternal(deleteIds)
            }

          }
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
    private fun getSongsWithSort( ids: List<Long>): Single<List<SongLists>> {
      return Single
        .fromCallable {
          if (ids.isEmpty()) {
            return@fromCallable Collections.emptyList<SongLists>()
          }

          val tempArray = Array(ids.size) { SongLists.SONG_LIST }

          tempArray
            .filter { it.id != SongLists.SONG_LIST.id }
        }
    }

  /**
   * 字符生成器
   * @param audioIds List<Long>
   * @return String
   */
  private fun makeInStr(audioIds: List<Long>): String {
    val inStrBuilder = StringBuilder(127)

    for (i in audioIds.indices) {
      inStrBuilder.append(audioIds[i]).append(if (i != audioIds.size - 1) "," else " ")
    }

    return inStrBuilder.toString()
  }
 companion object{
   private const val CUSTOMSORT = "CUSTOMSORT"
   @Volatile
  private var INSTANCE:DatabaseRepository?=null
   @JvmStatic
   fun getInstance():DatabaseRepository=
     INSTANCE?: synchronized(this){
       INSTANCE ?: DatabaseRepository()
     }

   private const val MAX_ARGUMENT_COUNT = 300
 }


}
