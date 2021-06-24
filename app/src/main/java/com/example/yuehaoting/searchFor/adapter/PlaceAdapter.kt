package com.example.yuehaoting.searchFor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.data.kugou.RecordData


class PlaceAdapter(private val placeList: List<RecordData>, private var mSearchHintInfo: SearchHintInfo) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_for, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        println("搜索RecyclerView----------------${placeList[0].HintInfo}")
        holder.placeName.text = place.HintInfo
        holder.itemView.setOnClickListener {
            println(place.HintInfo)
            mSearchHintInfo?.hinInfo(place.HintInfo)
        }
    }

    override fun getItemCount() = placeList.size

    interface SearchHintInfo {
        fun hinInfo(i: String)
    }


}
