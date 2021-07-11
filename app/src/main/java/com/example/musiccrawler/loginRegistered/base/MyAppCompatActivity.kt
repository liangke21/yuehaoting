package com.example.musiccrawler.loginRegistered.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

/**
 * 作者: QQ群:1396797522
 * 时间: 2021/5/27 13:02
 * 描述:
 */
open class MyAppCompatActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomTopNavigationBarSettings()
    }

    private fun bottomTopNavigationBarSettings() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //注释掉这行代码
                    //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            decorView.systemUiVisibility = option
            //设置导航栏（顶部和底部）颜色为透明，注释掉这行代码
            //getWindow().setNavigationBarColor(Color.TRANSPARENT);
            //设置通知栏颜色为透明
            window.statusBarColor = Color.TRANSPARENT
        }
        //隐藏导航栏
        //隐藏导航栏zz
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
    }
}