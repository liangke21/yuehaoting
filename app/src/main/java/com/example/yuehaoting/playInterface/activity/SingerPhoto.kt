package com.example.yuehaoting.playInterface.activity


import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.App.Companion.context
import com.example.yuehaoting.R
import com.example.yuehaoting.base.handler.HandlerMy
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhoto
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.util.Tag.singerPhoto
import timber.log.Timber


/**
 * 作者: 天使
 * 时间: 2021/7/4 15:51
 * 描述:
 */
object SingerPhoto {
    private val url = ArrayList<String>()
    fun singerPhotoUrl(data4: Result<List<SingerPhoto.Data.Imgs.Data4>>): ArrayList<String> {
        tryNull {
            val data: ArrayList<SingerPhoto.Data.Imgs.Data4> = data4.getOrNull() as ArrayList<SingerPhoto.Data.Imgs.Data4>
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


    //定义一个handler来进行隔时间操作
    private var handler: HandlerMy = HandlerMy("playUiPhoto")

    private var isRunnable: Boolean = false
    private lateinit var myRunnable: Runnable
    fun photoCycle(url: ArrayList<String>, fl: CoordinatorLayout, resources: Resources, block: (Bitmap, Boolean) -> Unit) {
        var count = -1
        if (url.size == 0) {
            Timber.tag(singerPhoto).v("0张图片的适合执行 :%s", url.size)
            Glide.with(context).asBitmap()
                .load(R.drawable.youjing)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        fl.background = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else {
            isRunnable = true
            myRunnable = Runnable {

                Timber.tag(singerPhoto).v("多少张图片链接 :%s", url.size)

                if (count == url.size - 1) {
                    count = -1
                }
                val gaga = ++count
                Timber.tag(singerPhoto).v("当前播放在第几张 :%s", gaga)
                Glide.with(context).asBitmap()
                    .load(url[gaga])
                    .override(context.width, context.height) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            fl.background = BitmapDrawable(resources, resource)
                            Timber.tag(singerPhoto).v("显示写真 :%s", resource.toString())
                            block(resource, false)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
                handler.setPostDelayed(myRunnable, 5000)
            }

            handler.post(myRunnable)

        }

    }

    fun handlerRemoveCallbacks() {
        if (isRunnable) {
            handler.setRemoveCallbacks(myRunnable)
        }
    }

}