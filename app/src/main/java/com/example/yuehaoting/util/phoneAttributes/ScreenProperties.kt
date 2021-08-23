package com.example.yuehaoting.util.phoneAttributes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import com.example.yuehaoting.util.Tag
import timber.log.Timber


/**
 * 作者: 天使
 * 时间: 2021/8/19 15:46
 * 描述:
 */
object ScreenProperties {

    @SuppressLint("ServiceCast")
    fun phoneAttributes(context: Context) :Array<Int>{
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getRealMetrics(metrics)
        val widthPixels = metrics.widthPixels
        val heightPixels = metrics.heightPixels
        val density = metrics.density

        val heightDp = heightPixels / density
        val widthDp = widthPixels / density
        val x = metrics.xdpi
        val y = metrics.ydpi
        var smallestWidthDP=0f
        smallestWidthDP = if (widthDp < heightDp) {
            widthDp
        }else {
            heightDp
        }

        Timber.tag(Tag.Display).v(
            "手机屏幕属性%s",
            "\n手机分辨率:       $heightPixels * $widthPixels px\n" +
                    "xdpi= :         $x dpi\n" +
                    "ydpi= :         $y dpi\n" +
                    "densityDpi:     ${metrics.densityDpi}\n" +
                    "density:        $density \n"+
                    "scaledDensity:  ${metrics.scaledDensity}\n" +
                    "heightDp:       $heightDp dp \n" +
                    "widthDp:        $widthDp dp  \n " +
                    "smallestWidthDP:$smallestWidthDP dp \n" +
                    "手机SDK 版本 : ${Build.VERSION.SDK_INT}"
        )
        return arrayOf(widthPixels,heightPixels)
    }




}