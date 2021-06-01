package com.example.yuehaoting.searchfor.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.searchfor.data.Title
import com.example.yuehaoting.searchfor.ui.adapter.TitleAdapter

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/1 10:20
 * 描述:
 */
class TitleFragment :Fragment(){
private lateinit var recyclerView: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_search_title,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        recyclerView=activity!!.findViewById(R.id.rv_search_title)
        val layoutManager=LinearLayoutManager(activity)
        layoutManager.orientation=LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager=layoutManager

        val titleData=resources.getStringArray(R.array.searchTitleArray)
        val adapter=ArrayList<Title>()
        titleData.forEach {
            adapter.add(Title((it)))
        }
        recyclerView.adapter=TitleAdapter( adapter)
    }
}