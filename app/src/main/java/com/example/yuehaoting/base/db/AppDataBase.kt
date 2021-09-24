package com.example.yuehaoting.base.db

import android.content.Context
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.yuehaoting.base.db.AppDataBase.Companion.VERSION
import com.example.yuehaoting.base.db.dao.PlayQueueDao
import com.example.yuehaoting.base.db.model.PlayQueue
import com.example.yuehaoting.util.Tag
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/22 22:19
 * 描述:
 */
@Database(
    entities = [
        PlayQueue::class
    ], version = VERSION, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun playQueueDao(): PlayQueueDao

    companion object {
        const val VERSION = 1

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context) :AppDataBase=

            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }

            }


        private fun buildDatabase(context: Context): AppDataBase {
            val migration1to3 = object : Migration(1, 1) { //版本迁移
                override fun migrate(database: SupportSQLiteDatabase) {

                }
            }

            val database = Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, "yuehaoting.db")
                //.addMigrations(migration1to3)
                .build()

            database.invalidationTracker.addObserver(object : InvalidationTracker.Observer(PlayQueue.TABLE_MAME) {
                override fun onInvalidated(tables: MutableSet<String>) {
                    Timber.tag(Tag.queueDatabase).v("onInvalidated %s",tables.toString())
                }
            })

            return database
        }
    }
}