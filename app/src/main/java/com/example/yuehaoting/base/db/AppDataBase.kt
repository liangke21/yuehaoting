package com.example.yuehaoting.base.db

import android.content.Context
import android.content.Intent
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.yuehaoting.base.db.AppDataBase.Companion.VERSION
import com.example.yuehaoting.base.db.dao.HistoryDao
import com.example.yuehaoting.base.db.dao.PlayQueueDao
import com.example.yuehaoting.base.db.model.HistoryQueue
import com.example.yuehaoting.base.db.model.PlayQueue
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.BroadcastUtil.sendLocalBroadcast
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.IntentUtil.makeCodIntent
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.EXTRA_PLAYLIST
import com.example.yuehaoting.util.MusicConstant.PLAYLIST_CHANGE
import com.example.yuehaoting.util.Tag
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/22 22:19
 * 描述:
 */
@Database(
    entities = [
        PlayQueue::class,
    HistoryQueue::class
    ], version = VERSION, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun playQueueDao(): PlayQueueDao

    abstract fun historyDao(): HistoryDao

    companion object {
        const val VERSION =5

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context) :AppDataBase=

            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }

            }


        private fun buildDatabase(context: Context): AppDataBase {
            val migration = object : Migration(4,5) { //版本迁移
                override fun migrate(database: SupportSQLiteDatabase) {

                }
            }

            val database = Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, "yuehaoting.db")
              .addMigrations(migration)
                .build()
             //观察那个表的数据发生变化
            database.invalidationTracker.addObserver(object : InvalidationTracker.Observer(PlayQueue.TABLE_MAME,HistoryQueue.HISTORY_QUEUE) {
                override fun onInvalidated(tables: MutableSet<String>) {

                    whenSurface(tables)

                }
            })

            return database
        }

    private    fun whenSurface(tables: MutableSet<String>){
               if (tables.contains(PlayQueue.TABLE_MAME)){
                   Timber.tag(Tag.queueDatabase).v("表中的数据发生变化 onInvalidated %s",tables.toString())
                   sendLocalBroadcast(Intent(PLAYLIST_CHANGE)
                       .putExtra(EXTRA_PLAYLIST,PlayQueue.TABLE_MAME)
                   )
            }else if (tables.contains(HistoryQueue.HISTORY_QUEUE)){
                Timber.v("历史记录表")

               }

        }


    }
}