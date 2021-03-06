package com.example.yuehaoting.searchFor.fragment.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.LazyBaseFragment
import com.example.yuehaoting.base.recyclerView.adapter.BaseRecyclerAdapter
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.FragmentMusicBinding
import com.example.yuehaoting.kotlin.launchMain
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.playInterface.activity.PlayActivity
import com.example.yuehaoting.searchFor.fragment.interfacet.HolderItemView
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment1ViewModel
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.EXTRA_POSITION
import com.example.yuehaoting.util.MusicConstant.KU_GOU
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 17:32
 * 描述:
 */
class SingleFragment1 : LazyBaseFragment() {
    private var _binding: FragmentMusicBinding? = null

    private val binding get() = _binding!!
    private val songLists = ArrayList<SongLists>()

    //第一次进入刷新
    private var isFirstEnter = true

    //第一次完成刷新
    private var isRefresh = true
    private var isLoadDataForTheFirstTime = true

    //列表适配器
    private var mAdapter: BaseRecyclerAdapter<KuGouSingle.Data.Lists>? = null
    private val viewModel by lazy { ViewModelProvider(this).get(SingleFragment1ViewModel::class.java) }


    //关键字
    private var keyword = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater)
        val data = activity!!.intent.getStringExtra("Single")
        //      Timber.v("Activity传输数据3 : %s", data.toString())
        viewModel.requestParameter(1, 10, data.toString())
        keyword = data.toString()
        return binding.root
    }

    private var page = 1

    //<editor-fold desc=" 懒加载" >
    override fun lazyInit() {

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()

        if (isFirstEnter) {
            isFirstEnter = false
            //binding.refreshLayout.autoRefresh()
            networkLoading()
            // Timber.v("刷新状态 %s", iss)
        }
        var id = 0L
        viewModel.singleObservedLiveData.observe(this) {
            tryNull {
                if (it.getOrNull() == null) {
                    networkAbnormalDisplay(false)
                    return@tryNull
                }
                networkAbnormalDisplay(true)

                val musicData = it.getOrNull() as KuGouSingle.Data
                Timber.v("酷狗音乐数据观察到:%s %s", musicData.lists[0].SongName, isLoadDataForTheFirstTime)
                val model = musicData.lists
                model.forEach {
                    val song = songDetails(it)
                    it.apply {
                        songLists.add(
                            SongLists(
                                id = ++id,
                                SongName = song[0]!!,
                                SingerName = song[1]!!,
                                FileHash = FileHash,
                                mixSongID = MixSongID,
                                lyrics = "",
                                album = AlbumName,
                                pic = "",
                                platform = KU_GOU
                            )
                        )
                    }
                }
                if (isLoadDataForTheFirstTime) {
                    isLoadDataForTheFirstTime = false
                    viewModel.songList.addAll(musicData.lists)
                    mAdapter = object : BaseRecyclerAdapter<KuGouSingle.Data.Lists>(viewModel.songList, R.layout.item_fragment_search_single_rv_content) {

                        override fun onBindViewHolder(holder: SmartViewHolder?, model: KuGouSingle.Data.Lists?, position: Int) {
                            val song = songDetails(model)
                            holder?.text(R.id.rv_fragment_search_Single_SongName, song[0])
                            holder?.text(R.id.rv_fragment_search_Single_AlbumName, song[1] + song[2])
                            songSoundQuality(holder, model)
                            playSong(holder, position)
                        }
                    }

                    if (isRefresh) {
                        isRefresh = false
                        binding.refreshLayout.finishRefresh()
                    }
                    recyclerView.adapter = mAdapter
                }

                if (page >= 2) {
                    mAdapter?.loadMore(musicData.lists)
                    binding.refreshLayout.finishLoadMore()
                }

                binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                    override fun onRefresh(refreshLayout: RefreshLayout) {
                        refreshLayout.layout.postDelayed({
                            isRefresh = true
                            isLoadDataForTheFirstTime = true
                            viewModel.songList.clear()
                            mAdapter?.notifyDataSetChangedMy()
                            viewModel.requestParameter(1, 10, keyword)
                            songLists.clear()
                        }, 0)
                    }

                    override fun onLoadMore(refreshLayout: RefreshLayout) {
                        ++page
                        Timber.v("酷狗音乐列表页数:%s", page)
                        viewModel.requestParameter(page, 10, keyword)
                    }
                })
            }

        }

    }
//</editor-fold>
    /**
     * 播放歌曲
     * @param holder SmartViewHolder?
     * @param position Int
     */
    private fun playSong(holder: SmartViewHolder?, position: Int) {
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
                MusicServiceRemote.setPlayQueue(songLists, IntentUtil.makeCodIntent(MusicConstant.PLAY_SELECTED_SONG).putExtra(EXTRA_POSITION, position))
            }
        }

    }

    @Deprecated("不可用,会打开一个Activity来播放", ReplaceWith("playSong(holder,position)", "playSong()"), level = DeprecationLevel.WARNING)
    private fun songPlay(holder: SmartViewHolder?, position: Int) {
        holder?.itemView?.setOnClickListener {
            Timber.v("酷狗列表角标:%s 歌曲名称:%s ", position, songLists[position].SingerName)
            if (songLists[position] == MusicServiceRemote.getCurrentSong()) {
                val intent = Intent(activity, PlayActivity::class.java)
                intent.putExtra(MusicConstant.CURRENT_SONG, songLists[position])  //向下一个Activity传入当前播放的歌曲
                activity?.startActivity(intent)
            } else {
                MusicServiceRemote.setPlayQueue(songLists, IntentUtil.makeCodIntent(MusicConstant.PLAY_SELECTED_SONG).putExtra(EXTRA_POSITION, position))

                val intent = Intent(activity, PlayActivity::class.java)
                intent.putExtra(MusicConstant.CURRENT_SONG, songLists[position])
                // intent.putExtra("isPlay",false)  作废  2021.9.12 |14.32
                activity?.startActivity(intent)
            }
        }
    }

    /**
     * @return songName 表示歌曲标题,singerName表示歌曲歌手,albumName 歌曲专辑
     **/
    private fun songDetails(model: KuGouSingle.Data.Lists?): Array<String?> {
        //歌曲名字
        var songName = model?.SongName
        if (model?.SongName?.contains("<em>") == true) {
            songName = songName?.replace("<em>", "")?.replace("</em>", "")
            println(songName)
        }
        //歌手
        var singerName = model?.SingerName
        if (singerName?.contains("<em>") == true) {
            singerName = singerName.replace("<em>", "").replace("</em>", "")

        }
        //专辑
        var albumName = model?.AlbumName

        if (albumName?.contains("<em>") == true) {
            albumName = albumName.replace("<em>", "").replace("</em>", "")

        }
        if (!TextUtils.isEmpty(albumName)) {
            albumName = "-《$albumName》"
        }

        return arrayOf(songName, singerName, albumName)
    }

    /**
     * 设置音质图标
     */
    private fun songSoundQuality(holder: SmartViewHolder?, model: KuGouSingle.Data.Lists?) {
        val hQFileHash = model?.HQFileHash
        if (!TextUtils.isEmpty(hQFileHash)) {
            holder?.image(R.id.iv_hq, R.drawable.search_hq)
        }
        val sQFileHash = model?.SQFileHash
        if (!TextUtils.isEmpty(sQFileHash)) {
            holder?.image(R.id.iv_sq, R.drawable.search_sq)
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
        isLoadDataForTheFirstTime = true
        _binding = null
        songLists.clear()
        mAdapter = null
    }
}







