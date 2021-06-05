package com.example.yuehaoting.base.log

import android.util.Log

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/4 9:05
 * 描述:
 */
object log {

   private  var isLog=true
    fun e(tag:String,msg:String){
        if (isLog){
            if (tag == ""){
                Log.e(this::class.java.simpleName,msg)
            }else{
                Log.e("${this::class.java.simpleName}$tag", msg)
            }
        }
    }

    fun e(msg:String){
        if (isLog){

            Log.e(this::class.java.simpleName,msg)

            }
        }

}