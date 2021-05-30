package com.example.yuehaoting.searchfor.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.searchfor.data.kugou.RecordData
import com.example.yuehaoting.searchfor.ui.fragment.PlaceFragment


class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<RecordData>) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        //val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_for, parent, false)
        val holder = ViewHolder(view)

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.HintInfo
       // holder.placeAddress.text = place.HintInfo
    }

    override fun getItemCount() = placeList.size

}
