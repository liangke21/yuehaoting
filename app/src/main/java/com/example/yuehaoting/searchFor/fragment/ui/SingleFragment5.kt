package com.example.yuehaoting.searchFor.fragment.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.LazyBaseFragment
import com.example.yuehaoting.base.recyclerView.adapter.BaseRecyclerAdapter
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.data.musicKuWo.KuWoList
import com.example.yuehaoting.databinding.FragmentMusicBinding
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.searchFor.fragment.interfacet.ListRefreshInterface
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment5ViewModel
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.KU_WO
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/8/10 9:23
 * 描述:
 */
class SingleFragment5 : LazyBaseFragment(), ListRefreshInterface {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    //第一次进入刷新
    private var isFirstEnter = true

    //第一次完成刷新
    private var isRefresh = true
    private var isLoadDataForTheFirstTime = true

    //列表适配器
    private var mAdapter: BaseRecyclerAdapter<KuWoList.KuWoListItem>? = null
    private val viewModel by lazyMy { ViewModelProvider(this).get(SingleFragment5ViewModel::class.java) }

    private var recyclerView: RecyclerView? = null

    //关键字
    private var keyword = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMusicBinding.inflate(inflater)
        val data = activity!!.intent.getStringExtra("Single")
        viewModel.requestParameter(1, 10, data.toString())
        keyword = data.toString()
        return binding.root
    }


    private var page = 1
    override fun lazyInit() {
        binding.refreshLayout.setEnableFooterFollowWhenNoMoreData(true)
        recyclerView = binding.recyclerView

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.itemAnimator = DefaultItemAnimator()

        if (isFirstEnter) {
            isFirstEnter = false
            binding.refreshLayout.autoRefresh()
        }
        viewModel.observedData.observe(this) {
            tryNull({

                if (it.getOrNull() == null) {
                    // catch 处理
                    //  "数据全部加载完毕".showToast(activity!!)
                    binding.refreshLayout.finishRefreshWithNoMoreData()
                    return@tryNull
                }
                val musicData = it.getOrNull() as KuWoList

                addData(musicData)

                Timber.v("酷我音乐数据观察到:%s %s", musicData[0].name, isLoadDataForTheFirstTime)
                if (isLoadDataForTheFirstTime) {
                    isLoadDataForTheFirstTime = false
                    viewModel.songList.addAll(musicData)

                    baseRecyclerAdapter()

                    if (isRefresh) {
                        isRefresh = false
                        binding.refreshLayout.finishRefresh()
                    }
                }

                if (page >= 2) {
                    mAdapter?.loadMore(musicData)
                    binding.refreshLayout.finishLoadMore()
                }

                refresh()

            }, {
                it.printStackTrace()
            })
        }
    }

    private var id = 0L
    private val songLists = ArrayList<SongLists>()
    private fun addData(musicData: KuWoList) {
        musicData.forEach {
            songLists.add(
                SongLists(
                    id = ++id,
                    SongName = it.name!!,
                    SingerName = songSinger(it),
                    FileHash = it.url_id!!,
                    mixSongID = "",
                    lyrics = it.lyric_id!!,
                    album = "",
                    pic = it.pic_id!!,
                    platform = KU_WO
                )
            )
        }
    }

    private fun songSinger(model: KuWoList.KuWoListItem): String {
        return if (model.album == "") {
            var ar = ""
            model.artist?.forEach {
                ar += "$it"
            }
            ar
        } else {
            var ar = ""
            model.artist?.forEach {
                ar += "$it"
            }
            ar
        }
    }


    override fun refresh() {

        binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                refreshLayout.layout.postDelayed({
                    isRefresh=true
                    isLoadDataForTheFirstTime=true
                    viewModel.songList.clear()
                    mAdapter?.notifyDataSetChangedMy()
                    viewModel.requestParameter(1, 10, keyword)
                    songLists.clear()
                }, 0)
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                ++page
                Timber.v("酷我音乐列表页数:%s", page)
                viewModel.requestParameter(page, 10, keyword)
            }
        })
    }

    override fun baseRecyclerAdapter() {
        mAdapter = object : BaseRecyclerAdapter<KuWoList.KuWoListItem>(viewModel.songList, R.layout.item_fragment_search_single_rv_content) {

            override fun onBindViewHolder(holder: SmartViewHolder?, model: KuWoList.KuWoListItem?, position: Int) {
                holder?.text(R.id.rv_fragment_search_Single_SongName, model?.name)

                holder?.text(R.id.rv_fragment_search_Single_AlbumName, songAndAlbum(model))
                songPlay(holder, position)
            }
        }
        recyclerView?.adapter = mAdapter
    }

    /**
     * 播放音乐
     * @param holder SmartViewHolder?
     * @param position Int
     */
    private fun songPlay(holder: SmartViewHolder?, position: Int) {
        val intent = Intent(MusicConstant.ACTION_CMD)
        holder?.itemView?.setOnClickListener {
            Timber.v("当前列表长度 %s", songLists.size)
            if (songLists[position] == MusicServiceRemote.getCurrentSong()) {
                intent.putExtra(
                    MusicConstant.EXTRA_CONTROL,
                    MusicConstant.PAUSE_PLAYBACK
                )
                BroadcastUtil.sendLocalBroadcast(intent)
            } else {
                MusicServiceRemote.setPlayQueue(songLists, IntentUtil.makeCodIntent(MusicConstant.PLAY_SELECTED_SONG).putExtra(MusicConstant.EXTRA_POSITION, position))
            }
        }
    }

    private fun songAndAlbum(model: KuWoList.KuWoListItem?): String {
        return if (model?.album == "") {
            var ar = ""
            model.artist?.forEach {
                ar += "$it-"
            }
            ar
        } else {
            var ar = ""
            model?.artist?.forEach {
                ar += "$it-"
            }
            ar + "《${model?.album}》"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoadDataForTheFirstTime = true
        _binding = null
        mAdapter = null
        recyclerView?.adapter = null
        recyclerView = null
        songLists.clear()
    }
}