package com.example.yuehaoting.base.recyclerView.typeAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者: LiangKe
 * 时间: 2021/10/2 18:27
 * 描述: 万能适配器RecyclerView
 */
abstract class CommonAdapter (private val page:Int): RecyclerView.Adapter<CommonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
     val layoutId=getLayoutId(viewType)

        return CommonViewHolder.getViewHolder(parent, layoutId)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        mOnBindViewHolder(holder,position)
    }

    override fun getItemCount(): Int {
        return page
    }

    abstract fun mOnBindViewHolder(holder: CommonViewHolder, position: Int)

    abstract fun getLayoutId( viewType: Int):Int

}