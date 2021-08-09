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
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.data.musicQQ.QQSongList
import com.example.yuehaoting.databinding.FragmentMusicBinding
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.kotlin.showToast
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment3ViewModel
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment4ViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/8/9 16:30
 * 描述:
 */
class SingleFragment4:BaseFragment() {

    private lateinit var binding: FragmentMusicBinding

    //第一次进入刷新
    private var isFirstEnter = true

    //第一次完成刷新
    private var isRefresh = true
    private var isLoadDataForTheFirstTime = true

    //列表适配器
    private lateinit var mAdapter: BaseRecyclerAdapter<QQSongList.Data.Song.Lists>
    private val viewModel by lazyMy { ViewModelProvider(this).get(SingleFragment4ViewModel::class.java) }

    //关键字
    private var keyword = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMusicBinding.inflate(inflater)
        val data = activity!!.intent.getStringExtra("Single")
        viewModel.requestParameter(1,10,data.toString())
        keyword = data.toString()
        return binding.root
    }

    override fun lazyOnResume() {

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
                val musicData = it.getOrNull() as QQSongList
                Timber.v("QQ音乐数据观察到:%s %s", musicData.data?.song?.list?.get(0)?.name, isLoadDataForTheFirstTime)
                if (isLoadDataForTheFirstTime) {
                    isLoadDataForTheFirstTime = false
                    musicData.data?.song?.list?.let { it1 -> viewModel.songList.addAll(it1) }
                    mAdapter = object : BaseRecyclerAdapter<QQSongList.Data.Song.Lists>(viewModel.songList, R.layout.item_fragment_search_single_rv_content) {

                        override fun onBindViewHolder(holder: SmartViewHolder?, model: QQSongList.Data.Song.Lists?, position: Int) {
                            holder?.text(R.id.rv_fragment_search_Single_SongName, model?.title)
                            holder?.text(R.id.rv_fragment_search_Single_AlbumName, model?.name)
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
                    mAdapter.loadMore(musicData.data?.song?.list)
                    binding.refreshLayout.finishLoadMore()
                }


                binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                    override fun onRefresh(refreshLayout: RefreshLayout) {
                        binding.refreshLayout.finishRefresh()
                        Timber.v("qq音乐列表刷新:%s", page)
                        binding.refreshLayout.resetNoMoreData()
                    }

                    override fun onLoadMore(refreshLayout: RefreshLayout) {
                        ++page
                        Timber.v("qq音乐列表页数:%s", page)
                        viewModel.requestParameter(page,10,keyword, )


                    }
                })
            },{
                // catch 处理
                "数据全部加载完毕".showToast(activity!!)
                binding.refreshLayout.finishLoadMore()
            })

        }


    }

}