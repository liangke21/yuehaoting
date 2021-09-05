package com.example.yuehaoting.mian.fragment1.newSongRecommendationFragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.MyFragment
import com.example.yuehaoting.base.glide.GlideApp
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/4 18:11
 * 描述:
 */
open class BaseFragmentNewSongRecommendation: MyFragment() {
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
                    Timber.v("resource%S", resource.toString())
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

}