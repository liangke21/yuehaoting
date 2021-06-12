package com.androidlk.baseactivity.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

open class SmMainActivity : AppCompatActivity() {
    /**获取 Activity ATG */
    private val  getActivityATG: String =this::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(getActivityATG,"onCreate 第一次创建了活动")

        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        Log.e(getActivityATG,"onStart 不可见变为可见")
        super.onStart()
    }

    override fun onResume(){
        Log.e(getActivityATG,"onResume 准备给用户交互,此时Activity处于栈顶,并且处于运行状态")
        super.onResume()
    }

    override fun onPause() {
        Log.e(getActivityATG,"onPause 准备去启动或恢另一个Activity的时候调用")
        super.onPause()
    }

    override fun onStop() {
        Log.e(getActivityATG,"onStop Activity完全不可见")
        super.onStop()
    }

    override fun onDestroy() {
        Log.e(getActivityATG,"onDestroy Activity销毁之前调用 ")
        super.onDestroy()
    }

    override fun onRestart() {
        Log.e(getActivityATG,"onRestart Activity停止状态变为运行状态 ")
        super.onRestart()
    }



}