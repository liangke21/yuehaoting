package com.example.yuehaoting.kotlin

import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.yuehaoting.data.kugou.RecordData
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/7/4 11:01
 * 描述:
 */
/**
 * 文件名: Try
 * 作者: 13967
 * 时间: 2021/7/4 11:01
 * 描述:  异常
 */

fun tryNull(block:()->Unit){
    try {
       block()
    } catch (e: NullPointerException) {
        e.printStackTrace()
        Timber.e("空指针异常 : %s",e)
    }catch (e:IndexOutOfBoundsException){
        Timber.e("索引越界异常: %s",e)
    }
}