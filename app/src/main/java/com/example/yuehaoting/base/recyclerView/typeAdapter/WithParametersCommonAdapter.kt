package com.example.yuehaoting.base.recyclerView.typeAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者: LiangKe
 * 时间: 2021/10/2 22:56
 * 描述:
 */
abstract class WithParametersCommonAdapter<T>(private val list: List<T>) : RecyclerView.Adapter<CommonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val layoutId=getLayoutId(viewType)

        return CommonViewHolder.getViewHolder(parent, layoutId)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        mOnBindViewHolder(list[position],holder,position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    abstract fun mOnBindViewHolder(model:T,holder: CommonViewHolder, position: Int)

    abstract fun getLayoutId( viewType: Int):Int

}