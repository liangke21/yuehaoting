package com.example.yuehaoting.base.activity

import android.view.View
import com.example.yuehaoting.statusBar.StatusBarUtil
import remix.myplayer.theme.ThemeStore.statusBarColor

/**
 * 作者: 天使
 * 时间: 2021/6/27 14:14
 * 描述:
 */
open class PlayBaseActivity:BaseActivity() {
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