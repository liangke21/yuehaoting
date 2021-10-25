package com.example.yuehaoting.main


import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.App.Companion.context
import com.example.yuehaoting.R
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhotoData
import com.example.yuehaoting.util.Tag.singerPhoto
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.concurrent.thread


/**
 * 作者: 天使
 * 时间: 2021/7/4 15:51
 * 描述:
 */
object MainSingerPhoto {
    private val url = ArrayList<String>()
    fun singerPhotoUrl(data: List<SingerPhotoData.Data.Imgs.Data4>): ArrayList<String> {
        try {
           // val data: ArrayList<SingerPhotoData.Data.Imgs.Data4> = data4.getOrNull() as ArrayList<SingerPhotoData.Data.Imgs.Data4>
            url.clear()
            data.forEach {
                Timber.v("数据长度 ${it.filename}:%s", it.filename.length)
                if (it.filename.length > 30) {
                    url.add(it.sizable_portrait)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return url
    }


    //定义一个handler来进行隔时间操作
    //   private var handler: HandlerMy = HandlerMy("playUiPhoto")
    @Deprecated("废弃")
    private var isRunnable: Boolean = false

    @Deprecated("废弃")
    private lateinit var myRunnable: Runnable

    @Deprecated("废弃线程播放,改成协程播放")
    fun photoCycle(url: ArrayList<String>, fl: LinearLayout, resources: Resources, block: (Bitmap, Boolean) -> Unit) {
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
                //handler.setPostDelayed(myRunnable, 5000)
            }

            //   handler.post(myRunnable)

        }

    }

    @Deprecated("废弃")
    fun handlerRemoveCallbacks() {
        if (isRunnable) {
            //    handler.setRemoveCallbacks(myRunnable)
        }
    }

    private var isPlayPhoto = true

    /**
     * 是否开启 幻影灯片
     * @param isPlay Boolean
     */
    fun setPlayPhoto(isPlay: Boolean) {
        Timber.v("是否幻影灯片 %s", isPlay)
        isPlayPhoto = isPlay
        if (!isPlay) {
            isLoadPicture = false
            urlList.clear()
        }
    }

    /**
     * 是否播放图片
     * @param isPlay Boolean
     */
    fun setPhoto(isPlay: Boolean):MainSingerPhoto {
        isLoadPicture = isPlay
        return this
    }


    private var urlList = ArrayList<String>()


    private var count = -1


    private var isLoadPicture = false

    private var singerId = ""

    /**
     * 设置图片合集
     * @param url ArrayList<String>
     */
    fun setUrlList(url: ArrayList<String>, singerId: String) {
        if (this.singerId == singerId) {
            Timber.tag(singerPhoto).v("还是当前歌曲 %s", url.size)
            return
        }
        Timber.tag(singerPhoto).v("添加幻影灯片数据 %s", url.size)
        if (url.size == 0) {


            return
        }
        urlList.clear()
        urlList.addAll(url)
        count = -1
        isLoadPicture = true
        this.singerId = singerId


    }

 private var timeMillis=5000L

   fun  setDelay(timeMillis:Long){
        this.timeMillis=timeMillis
    }
    suspend fun playCycleCoroutine(fl: LinearLayout, resources: Resources, block: (Bitmap, Boolean) -> Unit) {
        if (urlList.size == 0) {
            Timber.tag(singerPhoto).v("0张图片的适合执行 :%s", urlList.size)
            Glide.with(context).asBitmap()
                .load(R.drawable.youjing)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        fl.background = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }



        while (isPlayPhoto) {
            Timber.tag(singerPhoto).v("多少张图片链接 :%s", this.urlList.size)
            if (isLoadPicture) {
                if (urlList.size != 0) {
                    if (count == urlList.size - 1) {
                        count = -1
                    }
                    val gaga = ++count
                    Timber.tag(singerPhoto).v("当前播放在第几张 :%s", gaga)
                    Glide.with(context).asBitmap()
                        .load(urlList[gaga])
                        .override(context.width, context.height)
                        .centerCrop()
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                fl.background = BitmapDrawable(resources, resource)
                                Timber.tag(singerPhoto).v("显示写真成功")
                                block(resource, false)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                }


            }
            delay(timeMillis)
        }
    }

    fun playCycleThread(fl: LinearLayout, resources: Resources, block: (Bitmap, Boolean) -> Unit) {


        if (urlList.size == 0) {
            Timber.tag(singerPhoto).v("0张图片的适合执行 :%s", urlList.size)
            Glide.with(context).asBitmap()
                .load(R.drawable.youjing)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        fl.background = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }

        thread {
            while (isPlayPhoto) {
                Timber.tag(singerPhoto).v("多少张图片链接 :%s", this.urlList.size)
                if (isLoadPicture) {
                    if (count == urlList.size - 1) {
                        count = -1
                    }
                    val gaga = ++count
                    Timber.tag(singerPhoto).v("当前播放在第几张 :%s", gaga)
                    Glide.with(context).asBitmap()
                        .load(urlList[gaga])
                        .override(context.width, context.height)
                        .centerCrop()
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                fl.background = BitmapDrawable(resources, resource)
                                Timber.tag(singerPhoto).v("显示写真成功")
                                block(resource, false)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })

                }
                Thread.sleep(5000)
            }
        }

    }


}