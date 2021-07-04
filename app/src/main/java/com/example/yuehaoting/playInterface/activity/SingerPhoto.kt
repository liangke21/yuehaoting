package com.example.yuehaoting.playInterface.activity

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.R
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhoto
import com.example.yuehaoting.kotlin.tryNull
import timber.log.Timber
import kotlin.concurrent.thread

/**
 * 作者: 天使
 * 时间: 2021/7/4 15:51
 * 描述:
 */
object SingerPhoto {
    private val url = ArrayList<String>()
    fun singerPhotoUrl(data4: Result<Any>): ArrayList<String> {
 tryNull {
            val data = data4.getOrNull() as ArrayList<SingerPhoto.Data.Imgs.Data4>
            url.clear()
            data.forEach {
                Timber.v("数据长度 ${it.filename}:%s", it.filename.length)
                if (it.filename.length > 30) {
                    url.add(it.sizable_portrait)
                }
            }
 }
        return url
    }

    private var count = -1

    //定义一个handler来进行隔时间操作
    private val handler: Handler = Handler()
    fun photoCycle(url: ArrayList<String>, fl: FrameLayout, activity: Activity, resources: Resources) {

        if (url.size==0){
            Glide.with(activity).asBitmap()
                .load(R.drawable.youjing)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        fl.background = BitmapDrawable(resources, resource)
                    }
                })


        }else{
            val myRunnable = object : Runnable {
                override fun run() {
                    Timber.v("url长度 :%s", url.size)
                    Timber.v("count长度 :%s", count)
                    handler.postDelayed(this, 5000)
                    if (count == url.size - 1) {
                        count = -1
                    }

                    tryNull {
                        Glide.with(activity).asBitmap()
                            .load(url[++count])
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    fl.background = BitmapDrawable(resources, resource)
                                }
                            })
                    }
                }
            }
            handler.post(myRunnable)
        }


    }


}