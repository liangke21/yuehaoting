package com.example.yuehaoting.searchFor.fragment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.base.recyclerView.adapter.BaseRecyclerAdapter
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.Fragment3Music163Binding
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment2ViewModel
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment3ViewModel
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/7/21 13:18
 * 描述:
 */
class SingleFragment3 : BaseFragment(){

    private lateinit var binding: Fragment3Music163Binding

    //第一次加载数据
    private var isFirstEnter=true
    //列表适配器
    private lateinit var mAdapter:BaseRecyclerAdapter<SongLists>
private val viewModel by lazyMy { ViewModelProvider(this).get(SingleFragment3ViewModel::class.java) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= Fragment3Music163Binding.inflate(inflater)
        val data = activity!!.intent.getStringExtra("Single")
        viewModel.requestParameter(data.toString(),"name","netease",1)
        return binding.root
    }
    override fun lazyOnResume() {
        binding.refreshLayout.setEnableFooterFollowWhenNoMoreData(true)

        if (isFirstEnter){
            isFirstEnter=false
            binding.refreshLayout.autoRefresh()
        }


    }

    override fun lazyInit() {

        viewModel.observedData.observe(this, Observer {
            val musicData =it.getOrNull()
            if (musicData != null) {
                Timber.v("网易音乐请求数据:%s",musicData.data[1])
            }
        })
        var view=binding.recyclerView

        if (view is RecyclerView){

            val recyclerView=view as RecyclerView
            recyclerView.layoutManager=LinearLayoutManager(context)
            recyclerView.itemAnimator=DefaultItemAnimator()
          //  mAdapter=object :BaseRecyclerAdapter<SongLists>()
        }
    }


}