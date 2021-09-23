package com.example.yuehaoting.base.rxJava

import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/23 13:03
 * 描述:
 */
open class LogObserver : SingleObserver<Any> {
    private val tag = this::class.java.simpleName

    override fun onSuccess(value: Any) {
        Timber.tag(tag).v("onSuccess: %s", value.toString())
    }

    override fun onSubscribe(d: Disposable) {
        Timber.tag(tag).v("onSubscribe")
    }

    override fun onError(e: Throwable) {
        Timber.tag(tag).v("onError: %s", e.message)
    }
}