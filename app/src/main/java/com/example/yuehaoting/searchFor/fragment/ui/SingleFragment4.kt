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
import com.example.yuehaoting.data.musicQQ.QQSongList
import com.example.yuehaoting.databinding.FragmentMusicBinding
import com.example.yuehaoting.kotlin.launchMain
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.searchFor.fragment.interfacet.ListRefreshInterface
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment4ViewModel
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.QQ
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/8/9 16:30
 * 描述:
 */
class SingleFragment4:LazyBaseFragment() ,ListRefreshInterface{

    private  var _binding: FragmentMusicBinding?=null
    private val binding get() = _binding!!

    //第一次进入刷新
    private var isFirstEnter = true

    //第一次完成刷新
    private var isRefresh = true
    private var isLoadDataForTheFirstTime = true

    //列表适配器
    private  var mAdapter: BaseRecyclerAdapter<QQSongList.Data.Song.Lists>? =null
    private val viewModel by lazyMy { ViewModelProvider(this).get(SingleFragment4ViewModel::class.java) }

    private var recyclerView: RecyclerView? = null

    //关键字
    private var keyword = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMusicBinding.inflate(inflater)
        val data = activity!!.intent.getStringExtra("Single")
        viewModel.requestParameter(1,10,data.toString())
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
           // binding.refreshLayout.autoRefresh()
            networkLoading()
        }
       viewModel.observedData.observe(this) {
            tryNull( {

                if (it.getOrNull() == null) {
                    networkAbnormalDisplay(false)
                    binding.refreshLayout.finishRefreshWithNoMoreData()
                    return@tryNull
                }
                networkAbnormalDisplay(true)
                val musicData = it.getOrNull() as QQSongList
                Timber.v("QQ音乐数据观察到:%s %s", musicData.data?.song?.list?.get(0)?.name, isLoadDataForTheFirstTime)

                addList(musicData.data?.song?.list!!)

                if (isLoadDataForTheFirstTime) {
                    isLoadDataForTheFirstTime = false
                    musicData.data.song.list.let { it1 -> viewModel.songList.addAll(it1) }

                    baseRecyclerAdapter()

                    if (isRefresh) {
                        isRefresh = false
                        binding.refreshLayout.finishRefresh()
                    }
                }

                if (page >= 2) {
                    mAdapter?.loadMore(musicData.data.song.list)
                    binding.refreshLayout.finishLoadMore()
                }

                refresh()

            },{
                // catch 处理
               it.printStackTrace()
            })

        }


    }
    var id = 0L
val songLists=ArrayList<SongLists>()
    private fun addList(list: List<QQSongList.Data.Song.Lists>) {
          list.forEach {
               songLists.add(
                   SongLists(
                       id = ++id,
                       SongName = it.title!!,
                       SingerName = songTitle(it)!!,
                       FileHash = it.mid!!,
                       mixSongID = "",
                       lyrics = "",
                       album = "",
                       pic = it.album?.mid!!,
                       platform =QQ
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
            holderItemView.itemView()
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
                },0)
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                ++page
                Timber.v("qq音乐列表页数:%s", page)
                viewModel.requestParameter(page,10,keyword )


            }
        })
    }

    override fun baseRecyclerAdapter() {
        mAdapter = object : BaseRecyclerAdapter<QQSongList.Data.Song.Lists>(viewModel.songList, R.layout.item_fragment_search_single_rv_content) {

            override fun onBindViewHolder(holder: SmartViewHolder?, model: QQSongList.Data.Song.Lists?, position: Int) {
                holder?.text(R.id.rv_fragment_search_Single_SongName, model?.title)

                holder?.text(R.id.rv_fragment_search_Single_AlbumName,songAndAlbum(model))
                songPlay(holder, position)
            }
        }
        recyclerView?.adapter = mAdapter
    }
private fun songTitle(model: QQSongList.Data.Song.Lists?): String? {
    return if (model?.album?.name=="") {
        model.singer?.get(0)?.name
    }else{
        model?.singer?.get(0)?.name
    }
}

    private  fun songAndAlbum(model: QQSongList.Data.Song.Lists?): String? {
        return if (model?.album?.name=="") {
            model.singer?.get(0)?.name
        }else{
            model?.singer?.get(0)?.name+"-《${model?.album?.name}》"
        }
    }
    //<editor-fold desc="网络加载展示ui" >
    /**
     * 初始网络加载
     */
    private fun networkLoading() {
        binding.refreshLayout.setEnableLoadMore(false)
        binding.refreshLayout.setEnableRefresh(false)
        binding.isTheInternet.visibility = View.VISIBLE
        binding.isTheInternet.repeatCount = 30
        binding.isTheInternet.playAnimation()
    }

    /**
     * 网络异常展示
     */
    private fun networkAbnormalDisplay(isLL: Boolean) {

        if (isLL) {
            binding.isTheInternet.repeatCount = 1
            binding.isTheInternet.visibility = View.GONE
            binding.refreshLayout.setEnableLoadMore(true)
            binding.refreshLayout.setEnableRefresh(true)
        } else {
            launchMain {
                delay(5000)
                binding.refreshLayout.finishRefresh()
                binding.refreshLayout.setEnableLoadMore(false)
                binding.refreshLayout.setEnableRefresh(false)
                binding.isTheInternet.visibility = View.VISIBLE
                binding.isTheInternet.repeatCount = 30
                binding.isTheInternet.playAnimation()
                viewModel.requestParameter(1, 10, keyword)
            }
        }
    }

    //</editor-fold>
    override fun onDestroyView() {
        super.onDestroyView()
        isLoadDataForTheFirstTime=true
        _binding=null
        mAdapter=null
        recyclerView?.adapter=null
        recyclerView=null
        songLists.clear()

    }
}