package com.example.yuehaoting.base.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.ViewManager
import com.example.yuehaoting.R
import com.example.yuehaoting.util.Tag
import com.kongzue.dialogx.DialogX
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator
import timber.log.Timber


/**
 * 作者: 天使
 * 时间: 2021/8/1 9:37
 * 描述:
 */
open class InitApplication : Application() {
    init {

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white) //全局设置主题颜色
            ClassicsHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }

        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setDrawableSize(20f)
        }




    }

    override fun onCreate() {
        super.onCreate()

        //初始化
        DialogX.init(this)

    }
}