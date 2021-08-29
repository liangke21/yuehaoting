package com.example.yuehaoting.mian.livedata

import androidx.lifecycle.liveData
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.data.kugou.specialRecommend.SetSpecialRecommend
import com.example.yuehaoting.data.kugou.specialRecommend.SpecialRecommend
import kotlinx.coroutines.Dispatchers
import timber.log.Timber


/**
 * 作者: 天使
 * 时间: 2021/8/29 14:26
 * 描述:
 */
object Repository {

    fun kuGouSpecialRecommend(ssr: SetSpecialRecommend) = liveData(Dispatchers.IO) {

        val result: Result<SpecialRecommend> = try {

            val mkuGouSpecialRecommend = SongNetwork.kuGouSpecialRecommend(ssr)

            run {
                if (mkuGouSpecialRecommend.status == 1) {
                    Timber.v("酷狗特别推荐请求成功-------------------- :%s", mkuGouSpecialRecommend.status)
                } else {
                    Timber.e("酷狗特别推荐请求失败-------------------- :%s", mkuGouSpecialRecommend.status)
                }
                Result.success(mkuGouSpecialRecommend)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
        emit(result)
    }

}