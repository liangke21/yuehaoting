package com.example.yuehaoting.main.discover.fragment1.newSongRecommendationFragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.MyFragment
import com.example.yuehaoting.base.glide.GlideApp
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.util.NetworkUtils

/**
 * 作者: LiangKe
 * 时间: 2021/9/4 18:11
 * 描述:
 */
abstract class BaseFragmentNewSongRecommendation: MyFragment() {
    /**
     * 是有网络
     * @return Boolean
     */
       fun isNetWork():Boolean{
           return  NetworkUtils.isNetWorkAvailable(context!!)
       }
    /**
     * 子类工具
     * @param img String  图片连接
     * @param size String 图片大小
     * @param roundedCorners Int 图片圆角
     * @param holder SmartViewHolder
     * @param id Int 控件id
     */
    @SuppressLint("CheckResult")
    fun holderImage(img:String,size:String,roundedCorners:Int,holder: SmartViewHolder,id:Int) {
        //图片圆角
        val requestOptions = RequestOptions()
        // requestOptions.placeholder(R.drawable.ic_launcher_background)
        RequestOptions.circleCropTransform()
        requestOptions.transform(RoundedCorners(roundedCorners))


        val picUrl = img.replace("{size}", size)

        GlideApp.with(this).asBitmap()
            .apply(requestOptions)
            .load(picUrl)
            .placeholder(R.drawable.load_started)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    holder.image(id, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    holder.image(id, R.drawable.load_cleared)
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                    holder.image(id, placeholder)
                }
            })

    }
    private var isLoaded = false
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "lazyInit:!!!!!!! isLoaded=$isLoaded,isHidden=$isHidden")
        if (!isLoaded && !isHidden) {
            lazyInit()
            isLoaded = true
        }


    }

    abstract fun lazyInit()

}