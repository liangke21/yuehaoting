package com.example.yuehaoting.base.recyclerView.typeAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者: LiangKe
 * 时间: 2021/10/2 19:26
 * 描述:
 */
abstract  class CommonTypeAdapter<T>(val list: List<T>) : RecyclerView.Adapter<CommonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val layoutId=getLayoutId(viewType)

        return CommonViewHolder.getViewHolder(parent, layoutId)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        mOnBindViewHolder(list[position],holder,position,getItemViewType(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return mGetItemViewType(position)
    }

    abstract fun mOnBindViewHolder(model:T,holder: CommonViewHolder, position: Int,type:Int)

    abstract fun getLayoutId( viewType: Int):Int
    //多Type
    abstract fun mGetItemViewType(position: Int): Int
}