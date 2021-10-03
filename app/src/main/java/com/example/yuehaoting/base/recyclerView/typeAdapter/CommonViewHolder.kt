package com.example.yuehaoting.base.recyclerView.typeAdapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者: 天使
 * 时间: 2021/8/26 20:07
 * 描述:
 */
class CommonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var mView: SparseArray<View>? = SparseArray()

    /**
     * 获取Holder
     */
    companion object {
        fun getViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return CommonViewHolder(itemView)
        }
    }

    /**
     * 获取布局控件
     */
    private fun getView(viewId: Int): View? {
        var view = mView?.get(viewId)

        view.let {
            view = itemView.findViewById(viewId)
            mView?.put(viewId, view)
        }
        return view
    }

    /**
     * 设置文本
     */
    fun setText(viewId: Int, text: String) {

        (getView(viewId) as TextView).text = text
    }

    fun setText(viewId: Int):TextView{
       return (getView(viewId) as TextView)
    }

    fun setRecyclerView(viewId: Int):RecyclerView{
       return (getView(viewId) as RecyclerView)
    }

}