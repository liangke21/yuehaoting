package com.example.yuehaoting.base.recyclerView.customLengthAdapter

import androidx.annotation.LayoutRes
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder

/**
 * 作者: LiangKe
 * 时间: 2021/9/3 16:44
 * 描述:
 */
class NullAdapter<T>(@LayoutRes layoutId:Int,length:Int): CustomLengthRecyclerAdapter<T>(layoutId,length) {

    override fun onBindViewHolder(holder: SmartViewHolder?, model: T, position: Int) {

    }
}