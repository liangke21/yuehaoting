package com.example.yuehaoting.base.rxJava

import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 作者: LiangKe
 * 时间: 2021/9/23 13:04
 * 描述:
 */
object RxUtil {


    fun <T> applySingleScheduler(): SingleTransformer<T, T> {
        return SingleTransformer { upstream: Single<T> ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}