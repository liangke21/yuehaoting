package com.example.yuehaoting.searchFor.fragment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.searchFor.adapter.SingleFragment1Adapter
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.searchFor.viewmodel.SingleViewModel
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 17:32
 * 描述:
 */
class SingleFragment1: BaseFragment(){

    private val viewModel by lazy { ViewModelProviders.of(this).get( SingleViewModel::class.java) }
    private  var recyclerView: RecyclerView?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      /*  val sp=activity!!.getSharedPreferences("Song",Context.MODE_APPEND)
        val data=sp.getString("Single",null)*/
        val data=activity!!.intent.getStringExtra("Single")
        viewModel.singlePlaces(data.toString())
       Timber.v("Fragment接收数据 : %s" ,data)
        return inflater.inflate(R.layout.fragment1_search_single,container,false)
    }

    override fun lazyOnResume() {

    }

    override fun lazyInit() {


       Timber.v("SingleFragment1 懒加载")

        recyclerView= view?.findViewById(R.id.rv_fragment_search_Single)
        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager

        viewModel.singleObservedLiveData.observe(this, Observer { result ->
            val list=result.getOrNull() as ArrayList<KuGouSingle.Data.Lists>
          Timber.v("singleObservedLiveData请求的曲目数据已经观察到 : %s",list[0].SongName)
            viewModel.singleList.clear()
            viewModel.singleList.addAll(list)
            recyclerView?.adapter= SingleFragment1Adapter(viewModel.singleList,activity)

        })

    }







}