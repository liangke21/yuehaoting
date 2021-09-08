package com.example.yuehaoting.mian.fragment1.newSongRecommendationFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yuehaoting.R
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.base.recyclerView.customLengthAdapter.CustomLengthRecyclerAdapter
import com.example.yuehaoting.base.recyclerView.customLengthAdapter.NullAdapter
import com.example.yuehaoting.data.kugou.NewSong
import com.example.yuehaoting.databinding.MainFragment1FragmentBKugouBinding
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.mian.fragment1.viewModel.FragmentAKuGouViewModel
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/6 22:13
 * 描述:
 */
class Fragment1KuGou : BaseFragmentNewSongRecommendation(), ShowNewSongList {
    private lateinit var binding: MainFragment1FragmentBKugouBinding
    private val viewModel by lazyMy { ViewModelProvider(this).get(FragmentAKuGouViewModel::class.java) }

    private lateinit var mAdapter:CustomLengthRecyclerAdapter<NewSong.Data.Info>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MainFragment1FragmentBKugouBinding.inflate(layoutInflater)

        viewModel.kuGouSpecialRecommendViewModel(1,25)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun lazyInit() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView2.layoutManager = layoutManager

        // binding.refreshLayout.setEnableRefresh(false)   //禁用下拉刷新

        if (isNetWork()) {
            haveInternet()
        } else {
            noInternet()
        }

    }

    override fun haveInternet() {
        haveInternetShowNewSongList()
    }

    override fun noInternet() {
        TODO("Not yet implemented")
    }

    override fun haveInternetShowNewSongList() {
        mAdapter=NullAdapter(R.layout.main_fragment1_fragment_item,21)
        binding.recyclerView2.adapter=mAdapter

               viewModel.observedLiveData.observe(this){
                   val newSong=it.getOrNull()
                   viewModel.listLiveData.clear()
                   viewModel.listLiveData.addAll(newSong?.data?.info!!)
                   for (i in 0..4 ){
                       //每次都删除0索引
                       viewModel.listLiveData.removeAt(0)
                   }

                   binding.recyclerView2.adapter=null
                   binding.recyclerView2.adapter?.notifyDataSetChanged()
                 mAdapter.notifyDataSetChanged()
                   mAdapter =object :CustomLengthRecyclerAdapter<NewSong.Data.Info> (viewModel.listLiveData,R.layout.main_fragment1_fragment_item,21){
                       override fun onBindViewHolder(holder: SmartViewHolder?, model: NewSong.Data.Info?, position: Int) {
                               val img = model?.album_cover
                           img?.let { it1 -> holderImage(it1, "100", 20, holder!!, R.id.iv_main_fragment1_fragment_a_ku_gou_item) }
                               val listFilename = model?.filename?.split("- ")

                           holder?.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song, listFilename?.get(1))
                           holder?.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song_album, listFilename?.get(0))

                       }

                       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmartViewHolder {
                           return super.onCreateViewHolder(parent, viewType)
                       }

                   }
                  binding.recyclerView2.adapter=mAdapter



               }
    }

    override fun noInternetShowNewSongList() {
        TODO("Not yet implemented")
    }


}