package com.example.yuehaoting.searchFor.fragment.ui

import android.annotation.SuppressLint
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
import com.example.yuehaoting.data.musicMiGu.MiGuSearch
import com.example.yuehaoting.databinding.FragmentMusicBinding
import com.example.yuehaoting.kotlin.launchMain
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.searchFor.fragment.interfacet.ListRefreshInterface
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment6ViewModel
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.MI_GU
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/8/10 12:07
 * 描述:
 */
class SingleFragment6 : LazyBaseFragment() ,ListRefreshInterface{

    private var _binding: FragmentMusicBinding?=null

private val binding get() = _binding!!
    //第一次进入刷新
    private var isFirstEnter = true

    //第一次完成刷新
    private var isRefresh = true
    private var isLoadDataForTheFirstTime = true

    //列表适配器
    private  var mAdapter: BaseRecyclerAdapter<MiGuSearch.Music>?=null
    private val viewModel by lazyMy { ViewModelProvider(this).get(SingleFragment6ViewModel::class.java) }

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
           // binding.refreshLayout.autoRefresh()
            networkLoading()
        }
        viewModel.observedDataSearch.observe(this) {
            try {
                if (it.getOrNull() == null) {
                    networkAbnormalDisplay(false)
                    binding.refreshLayout.finishRefreshWithNoMoreData()
                    return@observe
                }
                networkAbnormalDisplay(true)
                val musicData = it.getOrNull() as MiGuSearch
           /*     if (musicData.musics?.isEmpty() == true) {
                    return@observe
                }*/
                Timber.v("咪咕音乐数据观察到:%s %s", musicData.musics?.get(0)?.songName, isLoadDataForTheFirstTime)

                addData(musicData.musics!!)
                if (isLoadDataForTheFirstTime) {
                    isLoadDataForTheFirstTime = false
                    viewModel.songListSearch.addAll(musicData.musics)

                    baseRecyclerAdapter()

                    if (isRefresh) {
                        isRefresh = false
                        binding.refreshLayout.finishRefresh()
                    }

                }

                if (page >= 2) {
                    mAdapter?.loadMore(musicData.musics)
                    binding.refreshLayout.finishLoadMore()
                }

                refresh()

            } catch (e: NullPointerException) {
                // catch 处理
               // "数据全部加载完毕".showToast(activity!!)
                binding.refreshLayout.finishRefreshWithNoMoreData()
                Timber.e("空指针异常 : %s", e.printStackTrace())
            } catch (e: IndexOutOfBoundsException) {
                if (page > 1) {
                   // "数据全部加载完毕".showToast(activity!!)
                    binding.refreshLayout.finishRefreshWithNoMoreData()
                }else{
                   // "没有该歌曲".showToast(activity!!)
                    binding.refreshLayout.finishRefreshWithNoMoreData()
                }

                Timber.e("索引越界异常: %s", e)
            } catch (e: ClassCastException) {
                Timber.e("转换异常: %s", e)
            } catch (e: IllegalArgumentException) {
                Timber.e("非法参数异常: %s", e)
            }

        }


    }

    private var id = 0L
    private val songLists = ArrayList<SongLists>()
    private fun addData(musics: List<MiGuSearch.Music>) {
        musics.forEach {
            songLists.add(
                SongLists(
                    id = ++id,
                    SongName = it.songName!!,
                    SingerName = it.artist!!,
                    FileHash = it.id!!,
                    mixSongID ="",
                    lyrics = it.mp3?:"",
                    album = it.albumName!!,
                    pic = it.cover ?:"",
                    platform = MI_GU
                )
            )
        }
    }


    override fun refresh() {
        binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onRefresh(refreshLayout: RefreshLayout) {
                refreshLayout.layout.postDelayed({
                    isRefresh=true
                    isLoadDataForTheFirstTime=true
                    viewModel.songListSearch.clear()
                    mAdapter?.notifyDataSetChangedMy()
                    viewModel.requestParameter(1, 10, keyword)
                    songLists.clear()
                  //  refreshLayout.finishRefresh()
                    Timber.v("咪咕音乐列表刷新:%s", page)
                   // refreshLayout.resetNoMoreData()
                },0)
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                ++page
                Timber.v("咪咕音乐列表页数:%s", page)
                viewModel.requestParameter(page, 10, keyword)

            }
        })
    }

    override fun baseRecyclerAdapter() {
        mAdapter = object : BaseRecyclerAdapter<MiGuSearch.Music>(viewModel.songListSearch, R.layout.item_fragment_search_single_rv_content) {

            override fun onBindViewHolder(holder: SmartViewHolder?, model: MiGuSearch.Music?, position: Int) {
                holder?.text(R.id.rv_fragment_search_Single_SongName, model?.songName)

                holder?.text(R.id.rv_fragment_search_Single_AlbumName, model?.artist+" - "+model?.albumName)
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
/*
    private fun songAndAlbum(model: MiGuList.MiGuListItem?): String {
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
*/
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
        songLists.clear()
        _binding = null
        recyclerView?.adapter=null
        recyclerView=null
        mAdapter=null
    }
}