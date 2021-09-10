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
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
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

    private var isLoadDataForTheFirstTime=true //第一次加载数据

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

        binding.refreshLayout.setEnableRefresh(false)   //禁用下拉刷新

       /* binding.refreshLayout.autoRefresh()
        binding.refreshLayout.finishRefresh()*/
        //binding.refreshLayout.setFooterInsetStartPx(200)

       // Timber.v(" binding.classicsFooter.isInEditMode%s", binding.classicsFooter.isInEditMode)
    // binding.refreshLayout. finishLoadMore(0,true,true)
      //  binding.refreshLayout.setNoMoreData(true)
       //todo 实现上拉加载数据



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
    private var page = 1
    override fun haveInternetShowNewSongList() {
        mAdapter=NullAdapter(R.layout.main_fragment1_fragment_item,20)
        binding.recyclerView2.adapter=mAdapter

               viewModel.observedLiveData.observe(this){
                   val newSong=it.getOrNull()
                   Timber.v("酷狗新歌推荐列表:%s", newSong)
                   if (isLoadDataForTheFirstTime){
                       isLoadDataForTheFirstTime=false

                       viewModel.listLiveData.clear()
                       viewModel.listLiveData.addAll(newSong?.data?.info!!)
                       for (i in 0..4 ){
                           //每次都删除0索引
                           viewModel.listLiveData.removeAt(0)
                       }

                       binding.recyclerView2.adapter=null

                       mAdapter.notifyDataSetChanged()
                       mAdapter =object :CustomLengthRecyclerAdapter<NewSong.Data.Info> (viewModel.listLiveData,R.layout.main_fragment1_fragment_item,20){
                           override fun onBindViewHolder(holder: SmartViewHolder?, model: NewSong.Data.Info?, position: Int) {
                               val img = model?.album_cover
                               img?.let { it1 -> holderImage(it1, "100", 20, holder!!, R.id.iv_main_fragment1_fragment_a_ku_gou_item) }
                               val listFilename = model?.filename?.split("- ")

                               holder?.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song, listFilename?.get(1))
                               holder?.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song_album, listFilename?.get(0))

                           }

                       }
                       binding.recyclerView2.adapter=mAdapter

                   }

                   if (page >= 2) {
                       mAdapter.loadMore(newSong?.data?.info!!,20)
                       binding.refreshLayout.finishLoadMore()//完成加载
                   }


                   binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                       override fun onRefresh(refreshLayout: RefreshLayout) {
                           refreshLayout.layout.postDelayed({
                               refreshLayout.finishRefresh() //完成刷新
                               Timber.v("酷狗新歌推荐列表刷新:%s", page)
                               refreshLayout.resetNoMoreData()
                           }, 2000)
                       }

                       override fun onLoadMore(refreshLayout: RefreshLayout) {
                           ++page
                           Timber.v("酷狗新歌推荐列表页数:%s", page)
                           viewModel.kuGouSpecialRecommendViewModel(page,20)


                       }
                   })


               }


    }

    override fun noInternetShowNewSongList() {
        TODO("Not yet implemented")
    }


}