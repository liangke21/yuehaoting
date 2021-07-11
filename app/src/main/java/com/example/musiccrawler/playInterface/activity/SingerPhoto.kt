package com.example.musiccrawler.playInterface.activity


import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.musiccrawler.App
import com.example.musiccrawler.R
import com.example.musiccrawler.base.handler.HandlerMy
import com.example.musiccrawler.data.kugouSingerPhoto.SingerPhoto
import com.example.musiccrawler.kotlin.tryNull
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
            val data :ArrayList<SingerPhoto.Data.Imgs.Data4> = data4.getOrNull() as ArrayList<SingerPhoto.Data.Imgs.Data4>
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

    private  var isRunnable:Boolean = false
    private lateinit var myRunnable : Runnable
    fun photoCycle(url: ArrayList<String>, fl: CoordinatorLayout, resources: Resources, block:(Bitmap)->Unit) {
        var count = -1
        if (url.size == 0) {
            Glide.with(App.context).asBitmap()
                .load(R.drawable.youjing)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        fl.background=BitmapDrawable(resources,resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else {
            isRunnable=true
             myRunnable = Runnable {
                 handler.setPostDelayed(myRunnable,5000)
                 Timber.v("url长度 :%s", url.size)
                 Timber.v("count长度 :%s", count)

                 Timber.v("----------------------------------------------------")
                 if (count == url.size - 1) {
                     count = -1
                 }
                 Glide.with(App.context).asBitmap()
                     .load(url[++count])
                     .into(object : CustomTarget<Bitmap>() {
                         override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                             fl.background=BitmapDrawable(resources,resource)
                             block(resource)
                         }

                         override fun onLoadCleared(placeholder: Drawable?) {}
                     })
             }

            handler.post(myRunnable)

        }

    }

   fun  handlerRemoveCallbacks (){
       if (isRunnable){
           handler.setRemoveCallbacks(myRunnable)
       }
   }
}