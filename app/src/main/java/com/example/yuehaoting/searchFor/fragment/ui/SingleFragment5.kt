package com.example.yuehaoting.searchFor.fragment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.base.recyclerView.adapter.BaseRecyclerAdapter
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.data.musicKuWo.KuWoList
import com.example.yuehaoting.databinding.FragmentMusicBinding
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.kotlin.showToast
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment5ViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/8/10 9:23
 * 描述:
 */
class SingleFragment5 :BaseFragment(){

    private lateinit var binding: FragmentMusicBinding

    //第一次进入刷新
    private var isFirstEnter = true

    //第一次完成刷新
    private var isRefresh = true
    private var isLoadDataForTheFirstTime = true

    //列表适配器
    private lateinit var mAdapter: BaseRecyclerAdapter<KuWoList.KuWoListItem>
    private val viewModel by lazyMy { ViewModelProvider(this).get(SingleFragment5ViewModel::class.java) }

    //关键字
    private var keyword = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMusicBinding.inflate(inflater)
        val data = activity!!.intent.getStringExtra("Single")
        viewModel.requestParameter(1,10,data.toString())
        keyword = data.toString()
        return binding.root
    }


    private var page = 1
    override fun lazyInit() {
        binding.refreshLayout.setEnableFooterFollowWhenNoMoreData(true)
        val recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()

        if (isFirstEnter) {
            isFirstEnter = false
            binding.refreshLayout.autoRefresh()
        }
        viewModel.observedData.observe(this) {
            tryNull( {
                val musicData = it.getOrNull() as KuWoList
                Timber.v("酷我音乐数据观察到:%s %s", musicData[0].name, isLoadDataForTheFirstTime)
                if (isLoadDataForTheFirstTime) {
                    isLoadDataForTheFirstTime = false
                    viewModel.songList.addAll(musicData)
                    mAdapter = object : BaseRecyclerAdapter<KuWoList.KuWoListItem>(viewModel.songList, R.layout.item_fragment_search_single_rv_content) {

                        override fun onBindViewHolder(holder: SmartViewHolder?, model: KuWoList.KuWoListItem?, position: Int) {
                            holder?.text(R.id.rv_fragment_search_Single_SongName, model?.name)

                            holder?.text(R.id.rv_fragment_search_Single_AlbumName,songAndAlbum(model))
                            holder?.itemView?.setOnClickListener {

                                Timber.v("歌曲角标:%s 歌曲名称:%s", position, model?.name)
                            }
                        }
                    }

                    if (isRefresh) {
                        isRefresh = false
                        binding.refreshLayout.finishRefresh()
                    }
                    recyclerView.adapter = mAdapter
                }

                if (page >= 2) {
                    mAdapter.loadMore(musicData)
                    binding.refreshLayout.finishLoadMore()
                }


                binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                    override fun onRefresh(refreshLayout: RefreshLayout) {
                        refreshLayout.layout.postDelayed({
                            refreshLayout.finishRefresh()
                            Timber.v("酷我音乐列表刷新:%s", page)
                            refreshLayout.resetNoMoreData()
                        },2000)
                    }

                    override fun onLoadMore(refreshLayout: RefreshLayout) {
                        ++page
                        Timber.v("酷我音乐列表页数:%s", page)
                        viewModel.requestParameter(page,10,keyword)


                    }
                })
            },{
                // catch 处理
                "数据全部加载完毕".showToast(activity!!)
                binding.refreshLayout.finishLoadMore()
            })

        }


    }

   private fun songAndAlbum(model: KuWoList.KuWoListItem?): String{
        return if (model?.album =="") {
            var ar=""
            model.artist?.forEach {
                ar+="$it-"
            }
          ar
        }else{
            var ar=""
            model?.artist?.forEach {
                ar+= "$it-"
            }
           ar+"《${model?.album}》"
        }
    }
}