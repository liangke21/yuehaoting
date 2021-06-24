package com.example.yuehaoting.musicPath


import android.content.Context
import android.widget.Toast
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

fun CoroutineScope.tryLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend () -> Unit,
    catch: ((e: Exception) -> Unit)? = {
        Timber.w(it)
    },
    catch2: ((e: IllegalArgumentException) -> Unit)? = {
        Timber.w(it)
    }
) {
    launch(context, start) {
        try {
            block()
        } catch (e: Exception) {
            catch?.invoke(e)
        } catch (e: IllegalArgumentException) {
            catch2?.invoke(e)
        }
    }

}

fun CoroutineScope.tryLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend () -> Unit
) {
    tryLaunch(context = context,
        start = start,
        block = block,
        catch = {
            Timber.v(it)
        })
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()

}
