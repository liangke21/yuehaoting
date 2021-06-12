package com.example.yuehaoting.musicpath


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 文件名: Ext
 * 作者: 13967
 * 时间: 2021/6/12 11:12
 * 描述:
 */

/**
 * 异常封装
 */
fun CoroutineScope.tryLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend () -> Unit,
    catch: ((e: Exception) -> Unit)? = {
        Timber.w(it)
    }
) {
    launch(context, start) {
        try {
            block()
        } catch (e: Exception) {
            catch?.invoke(e)
        }
    }
}


