package com.example.musiccrawler.searchFor.fragment.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musiccrawler.R
import com.example.musiccrawler.searchFor.adapter.SingleFragment1Adapter
import com.example.musiccrawler.data.kugousingle.KuGouSingle
import com.example.musiccrawler.searchFor.fragment.BaseFragment
import com.example.musiccrawler.searchFor.viewmodel.SingleViewModel

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 22:29
 * 描述:
 */
class Fragment2:BaseFragment() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(SingleViewModel::class.java) }
    private  var recyclerView: RecyclerView?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sp=activity!!.getSharedPreferences("Song", Context.MODE_APPEND)
        val data=sp.getString("Single",null)
        viewModel.singlePlaces(data.toString())

        return inflater.inflate(R.layout.fragment_login2,container,false)

    }

    override fun lazyInit() {

        recyclerView=view?.findViewById(R.id.rv_fragment_search_Single2)
        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager

        viewModel.singleObservedLiveData.observe(this, Observer { result ->
            val list=result.getOrNull() as ArrayList<KuGouSingle.Data.Lists>
            viewModel.singleList.addAll(list)
            recyclerView?.adapter= SingleFragment1Adapter(viewModel.singleList, activity)
        })


    }
}