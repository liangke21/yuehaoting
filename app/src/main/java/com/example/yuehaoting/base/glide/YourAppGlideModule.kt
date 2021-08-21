package com.example.yuehaoting.base.glide

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule

/**
 * 作者: QQ群:799145059
 * 时间: 2021/7/3 12:47
 * 描述:
 */
@GlideModule
class YourAppGlideModule: AppGlideModule (){
    override fun applyOptions(context: Context, builder: GlideBuilder) {
      val diskCacheSizeBytes = 1024 *1024 *100
        builder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(context,"Photo", diskCacheSizeBytes.toLong()))
    }
}

