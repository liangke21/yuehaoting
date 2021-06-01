package com.example.yuehaoting.searchfor.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.searchfor.data.Title

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/1 10:55
 * 描述:
 */
class TitleAdapter(private val list: List<Title>):RecyclerView.Adapter<TitleAdapter.ViewHolder>() {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val tVTitle: TextView =view.findViewById(R.id.tv_title_bar_search)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view=LayoutInflater.from(parent.context).inflate(R.layout.item_search_title,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val mList=list[position]
        println(mList.title)
        holder.tVTitle.text=mList.title
    }

    override fun getItemCount(): Int {
       return list.size
    }
}