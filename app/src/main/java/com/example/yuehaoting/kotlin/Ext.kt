package com.example.yuehaoting.kotlin


import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty


/**
 * 文件名: Ext
 * 作者: 13967
 * 时间: 2021/6/12 11:12
 * 描述:
 */

//协程作用域

fun launchMy(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend () -> Unit,
){
    val c= CoroutineScope(context)
    c.launch(context,start) {
        block()
    }
}

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
        Timber.e(it)
    },
    catch2: ((e: IllegalArgumentException) -> Unit)? = {
        Timber.e(it)
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
            Timber.e(it)
        })
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()

}

//-------------------------------------------SharedPreferences------------------------------------------------------------------------------------------

fun setSp(context: Context, data:String="data",block: SharedPreferences.Editor.() -> Unit ) {
    val sp = context.getSharedPreferences(data, Context.MODE_PRIVATE)
    val editor = sp.edit()
    editor.block()
    editor.apply()

}

inline fun<T> getSp(context: Context,data:String="data",block: SharedPreferences.() -> T): T {
    val sp = context.getSharedPreferences(data, Context.MODE_PRIVATE)
   return sp.block()
}
//-------------------------------------------SharedPreferences------------------------------------------------------------------------------------------

//-------------------------------------------lazy------------------------------------------------------------------------------------------
class Later<T> (val block:()->T){

    var value:Any?=null
    operator fun getValue(any: Any?,prop:KProperty<*>):T{
        if (value==null){
            value=block()
        }
        return value as T
    }
    operator fun  setValue(any: Any?,prop:KProperty<*>,v:T){
        value=v
    }

}

fun <T> lazyMy(block: () -> T)=Later(block)

//-------------------------------------------lazy------------------------------------------------------------------------------------------


