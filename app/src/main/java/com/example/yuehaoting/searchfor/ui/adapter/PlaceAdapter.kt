package com.example.yuehaoting.searchfor.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.searchfor.data.kugou.RecordData
import com.example.yuehaoting.searchfor.ui.fragment.PlaceFragment


class PlaceAdapter( private val placeList: List<RecordData>,private  var mSearchHintInfo: SearchHintInfo) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_for, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.HintInfo
        holder.itemView.setOnClickListener{
            mSearchHintInfo?.hinInfo(place.HintInfo)
        }
    }

    override fun getItemCount() = placeList.size

    interface SearchHintInfo{
        fun hinInfo(i:String)
    }

}
