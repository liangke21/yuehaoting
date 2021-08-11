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
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.base.recyclerView.adapter.BaseRecyclerAdapter
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.FragmentMusicBinding
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.playInterface.activity.PlayActivity
import com.example.yuehaoting.searchFor.adapter.SingleFragment1Adapter
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment1ViewModel
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.EXTRA_POSITION
import com.example.yuehaoting.util.MusicConstant.KU_GOU
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 17:32
 * 描述:
 */
class SingleFragment1: BaseFragment(){
    private lateinit var binding: FragmentMusicBinding

    private val musicUtil = IntentUtil()

    private val songLists = ArrayList<SongLists>()
    //第一次进入刷新
    private var isFirstEnter = true

    //第一次完成刷新
    private var isRefresh = true
    private var isLoadDataForTheFirstTime = true
    //列表适配器
    private lateinit var mAdapter: BaseRecyclerAdapter<KuGouSingle.Data.Lists>
    private val viewModel by lazy { ViewModelProvider(this).get(SingleFragment1ViewModel::class.java) }
    //关键字
    private var keyword = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicBinding.inflate(inflater)
        val data = activity!!.intent.getStringExtra("Single")
        Timber.v("Activity传输数据3 : %s", data.toString())
        viewModel.requestParameter(1, 10, data.toString())
        keyword = data.toString()
        return binding.root
    }

    override fun lazyOnResume() {

    }
    private var page = 1

    override fun lazyInit() {

        val recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()

        if (isFirstEnter) {
            isFirstEnter = false
            binding.refreshLayout.autoRefresh()
        }

        viewModel.singleObservedLiveData.observe(this) {
            tryNull {
                val musicData = it.getOrNull() as KuGouSingle.Data
                Timber.v("酷狗音乐数据观察到:%s %s", musicData.lists[0].SongName, isLoadDataForTheFirstTime)
                if (isLoadDataForTheFirstTime) {
                    isLoadDataForTheFirstTime = false
                    viewModel.songList.addAll(musicData.lists)
                    mAdapter = object : BaseRecyclerAdapter<KuGouSingle.Data.Lists>(viewModel.songList, R.layout.item_fragment_search_single_rv_content) {

                        override fun onBindViewHolder(holder: SmartViewHolder?, model: KuGouSingle.Data.Lists?, position: Int) {

                            val song = songDetails(model)
                            holder?.text(R.id.rv_fragment_search_Single_SongName, song[0])
                            holder?.text(R.id.rv_fragment_search_Single_AlbumName, song[1] + song[2])

                            songSoundQuality(holder, model)

                            model?.apply {
                                songLists.add(
                                    SongLists(
                                        SongName=song[0]!!,
                                        SingerName=SingerName,
                                        FileHash=FileHash,
                                        mixSongID=MixSongID,
                                        lyrics = "",
                                        album = AlbumName,
                                        pic = "",
                                        platform = KU_GOU
                                        )
                                )
                            }
                            songPlay(holder,songLists,position,model?.MixSongID!!)

                        }
                    }

                    if (isRefresh) {
                        isRefresh = false
                        binding.refreshLayout.finishRefresh()
                    }
                    recyclerView.adapter = mAdapter
                }

                if (page >= 2) {
                    mAdapter.loadMore(musicData.lists)
                    binding.refreshLayout.finishLoadMore()
                }


                binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                    override fun onRefresh(refreshLayout: RefreshLayout) {
                        refreshLayout.layout.postDelayed({
                            refreshLayout.finishRefresh()
                            Timber.v("酷狗音乐列表刷新:%s", page)
                            refreshLayout.resetNoMoreData()
                        }, 2000)
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

    private fun songPlay(holder: SmartViewHolder?,  songLists: ArrayList<SongLists>, position: Int, mixSongID: String) {
        holder?.itemView?.setOnClickListener {
            Timber.v("歌曲角标:%s 歌曲名称:%s", position, songLists[0].SongName)
            Timber.d("后台播放1")
            if(MusicServiceRemote.isPlaying() && songLists[position]== MusicServiceRemote.getCurrentSong()){
                val intent= Intent(activity, PlayActivity::class.java)
                intent.putExtra(MusicConstant.CURRENT_SONG,songLists[position])
                activity?.startActivity(intent)
            }else{
                MusicServiceRemote.setPlayQueue(songLists, musicUtil.makeCodIntent(MusicConstant.PLAY_SELECTED_SONG).putExtra(EXTRA_POSITION, position))
                val intent= Intent(activity, PlayActivity::class.java)
                intent.putExtra(MusicConstant.SINGER_ID,mixSongID)
                intent.putExtra(MusicConstant.CURRENT_SONG,songLists[position])
                activity?.startActivity(intent)
            }
        }
    }

    /**
     * @return songName 表示歌曲标题,singerName表示歌曲歌手,albumName 歌曲专辑
     **/
    private fun songDetails( model: KuGouSingle.Data.Lists?): Array<String?> {
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
            holder?.image(R.id.iv_hq,R.drawable.search_hq)
        }
        val sQFileHash = model?.SQFileHash
        if (!TextUtils.isEmpty(sQFileHash)) {
            holder?.image(R.id.iv_sq,R.drawable.search_sq)
        }
    }
}







