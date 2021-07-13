package com.example.yuehaoting.searchFor.adapter

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musiccrawler.hifini.DataSearch
import com.example.yuehaoting.R

/**
 * 作者: 天使
 * 时间: 2021/7/13 16:23
 * 描述:
 */
class SingleFragment2Adapter(private val list: List<DataSearch.Attributes>):  RecyclerView.Adapter<SingleFragment2Adapter.ViewHolder>() {

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val songName: TextView =view.findViewById(R.id.tv_search_hifIni_song_title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.item_fragment_hifini2,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.songName.text=list[position].songTitle
    }

    override fun getItemCount(): Int {
        return list.size
    }
}