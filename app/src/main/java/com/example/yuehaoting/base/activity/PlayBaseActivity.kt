package com.example.yuehaoting.base.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.View
import com.example.yuehaoting.theme.StatusBarUtil
import com.example.yuehaoting.theme.ThemeStore.statusBarColor
import com.example.yuehaoting.util.Tag
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/6/27 14:14
 * 描述:
 */
open class PlayBaseActivity:BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setSatuBarColor()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setSatuBarColor()
    }

    /**
     * 设置状态栏颜色
     */
    protected open fun setSatuBarColor(){
       StatusBarUtil.setColorNoTranslucent(this, statusBarColor)
    }









}