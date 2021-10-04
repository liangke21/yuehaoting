package com.example.yuehaoting.base.db

import com.example.yuehaoting.App
import com.example.yuehaoting.base.db.model.HistoryQueue
import com.example.yuehaoting.searchFor.adapter.data.History
import io.reactivex.Single
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/30 9:58
 * 描述:
 */
class HistoryRepository {

    private val dp=AppDataBase.getInstance(App.context.applicationContext)

    /**
     * 添加数据
     * @param name String
     */
 fun integerData(name: String){



     val historyQueue=HistoryQueue(0,name)


     dp.historyDao().insertHistory(historyQueue)
 }

    /**
     * 获取 历史记录
     * @return Single<List<History>>
     */
    fun getHistory(): Single<List<History>>{
       val history=ArrayList<History>()
        return Single.fromCallable {
            dp.historyDao().selectAll()

        }.flatMap {
            historyWithSort(it)
        }

    }

    private fun historyWithSort(ids:List<HistoryQueue>):Single<List<History>>{


        return Single.fromCallable {
            ids.map {
                History(
                    name = it.name
                )
            }
        }
    }

    /**
     * 移除数据
     * @param name String
     * @return Single<Int>
     */
    fun removeData(name:String):Single<Int>{
        Timber.v("删除数据库中的HistoryQueue表中的1 name字段 %s",name)
      return Single.fromCallable {
          Timber.v("删除数据库中的HistoryQueue表中的2 name字段 %s",name)
          deleteData(name)
      }
    }

    /**
     * 删除数据
     */
    private fun deleteData(name:String):Int{
        Timber.v("删除数据库中的HistoryQueue表中的 name字段 %s",name)
        return dp.historyDao().delete(name)

    }
    companion object{

        @Volatile
        private var INSTANCE: HistoryRepository? = null

        @JvmStatic
        fun  getInstanceHistory(): HistoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HistoryRepository()
            }

    }
}