package com.example.yuehaoting.searchfor.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.searchfor.adapter.SingleFragment1dapter
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.searchfor.viewmodel.SingleViewModel

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 17:32
 * 描述:
 */
class SingleFragment1( private val mDataList:List<String>):BaseFragment(){

    private val viewModel by lazy { ViewModelProviders.of(this).get( SingleViewModel::class.java) }
    private  var recyclerView: RecyclerView?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sp=activity!!.getSharedPreferences("Song",Context.MODE_APPEND)
        val data=sp.getString("Single",null)
        viewModel.singlePlaces(data.toString())

        return inflater.inflate(R.layout.fragment1_search_single,container,false)
    }

    override fun lazyInit() {


        Toast.makeText(context, "123", Toast.LENGTH_SHORT).show();

        recyclerView= view?.findViewById(R.id.rv_fragment_search_Single)
        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager

        viewModel.singleObservedLiveData.observe(this, Observer { result ->
            val list=result.getOrNull() as ArrayList<KuGouSingle.Data.Lists>
            Log.e("请求的曲目数据已经观察到",list[0].SongName)
            viewModel.singleList.addAll(list)
            recyclerView?.adapter= SingleFragment1dapter(viewModel.singleList)

        })

    }







}