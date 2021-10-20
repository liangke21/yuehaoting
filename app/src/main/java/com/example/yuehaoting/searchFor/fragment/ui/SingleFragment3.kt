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
import com.example.yuehaoting.base.recyclerView.adapter.BaseRecyclerAdapter
import com.example.yuehaoting.base.fragmet.LazyBaseFragment
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.data.music163.MusicData
import com.example.yuehaoting.databinding.FragmentMusicBinding
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.searchFor.fragment.interfacet.ListRefreshInterface
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment3ViewModel
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.MUSIC_136
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/7/21 13:18
 * 描述:
 */
class SingleFragment3 : LazyBaseFragment(), ListRefreshInterface {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    //第一次进入刷新
    private var isFirstEnter = true

    //第一次完成刷新
    private var isRefresh = true
    private var isLoadDataForTheFirstTime = true

    //列表适配器
    private var mAdapter: BaseRecyclerAdapter<MusicData.Data>? = null
    private val viewModel by lazyMy { ViewModelProvider(this).get(SingleFragment3ViewModel::class.java) }

    private var recyclerView: RecyclerView? = null

    //关键字
    private var keyword = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMusicBinding.inflate(inflater)
        val data = activity!!.intent.getStringExtra("Single")
        //viewModel.requestParameter(data.toString(), "name", "netease", 1)
        data.toString()
        viewModel.requestParameter("10", "1", data.toString())
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
        viewModel.observedData163.observe(this) {
            tryNull({
                if (it.getOrNull() == null) {
                    binding.refreshLayout.finishRefreshWithNoMoreData()
                    return@tryNull
                }
                val musicData = it.getOrNull()?.map {
                    MusicData.Data(
                        type = "",
                        link = "",
                        songid = it.url_id,
                        title = it.name,
                        author = singer(it.artist),
                        lrc = "${it.lyric_id}",
                        url = "",
                        pic = it.pic_id
                    )
                }
               addList(musicData!!)

                musicData[0].let { it1 -> Timber.v("网易音乐数据观察到:%s %s", it1.author, isLoadDataForTheFirstTime) }
                if (isLoadDataForTheFirstTime) {
                    isLoadDataForTheFirstTime = false
                    musicData.let { it1 -> viewModel.songList.addAll(it1) }

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
    val songLists=ArrayList<SongLists>()
    var id = 0L
    private fun addList(model: Collection<MusicData.Data>) {
        model.forEach {

            songLists.add(
                SongLists(
                    id = ++id,
                    SongName = it.title!!,
                    SingerName = it.author!!,
                    FileHash = it.songid!!.toString(),
                    mixSongID = "",
                    lyrics = "",
                    album = "",
                    pic = it.pic!!,
                    platform = MUSIC_136
                )
            )
        }
    }

    /**
     * 播放音乐
     * @param holder SmartViewHolder?
     * @param position Int
     */
    private  fun  songPlay(holder: SmartViewHolder?, position: Int){
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

    private fun singer(artist: List<String?>?): String {
        var str = ""
        if (artist != null) {
            for (i in artist.indices) {
                str += if (i == 0) {
                    artist[i]
                } else {
                    " - ${artist[i]}"
                }
            }
        } else {
            return ""
        }
        return str
    }

    override fun refresh() {
        binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                refreshLayout.layout.postDelayed({
                    refreshLayout.finishRefresh()
                    Timber.v("网易音乐列表刷新:%s", page)
                    refreshLayout.resetNoMoreData()
                }, 2000)
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                ++page
                Timber.v("网易音乐列表页数:%s", page)
                viewModel.requestParameter("10", "$page", keyword)

            }
        })
    }

    override fun baseRecyclerAdapter() {
        mAdapter = object : BaseRecyclerAdapter<MusicData.Data>(viewModel.songList, R.layout.item_fragment_search_single_rv_content) {

            override fun onBindViewHolder(holder: SmartViewHolder?, model: MusicData.Data?, position: Int) {
                holder?.text(R.id.rv_fragment_search_Single_SongName, model?.title)
                holder?.text(R.id.rv_fragment_search_Single_AlbumName, model?.author)
                songPlay(holder, position)
            }
        }
        recyclerView?.adapter = mAdapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mAdapter = null
        recyclerView?.adapter = null
        recyclerView = null
        songLists.clear()
        isLoadDataForTheFirstTime = true
    }
}