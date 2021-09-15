package com.example.yuehaoting.main.fragment1.liveData

import androidx.lifecycle.liveData
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.data.kugou.NewSong
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/4 19:21
 * 描述:
 */
object Repository {

    fun kuGouNewSongRepository(page:Int, size:Int) = liveData(Dispatchers.IO) {

        val result: Result<NewSong> = try {

            val kuGouNewSong = SongNetwork.kuGouNewSongSongNetwork(page, size)

            run {
                if (kuGouNewSong.status == 1) {
                    Timber.v("酷狗新歌推荐请求成功-------------------- :%s", kuGouNewSong.status)
                } else {
                    Timber.e("酷狗新歌推荐请求失败-------------------- :%s", kuGouNewSong.status)
                }
                Result.success(kuGouNewSong)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
        emit(result)
    }

}