package com.example.yuehaoting.main.discover.fragment1.newSongRecommendationFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yuehaoting.R
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.base.recyclerView.customLengthAdapter.CustomLengthRecyclerAdapter
import com.example.yuehaoting.base.recyclerView.customLengthAdapter.NullAdapter
import com.example.yuehaoting.data.kugou.NewSong
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.MainNavigationDiscoverFragment1KugouBinding
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.kotlin.setSp
import com.example.yuehaoting.main.discover.fragment1.viewModel.FragmentAKuGouViewModel
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.playInterface.activity.PlayActivity
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.NEW_SONG_KU_GOU
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/6 22:13
 * 描述: 新歌推荐 酷狗
 */
class Fragment1KuGou : BaseFragmentNewSongRecommendation(), ShowNewSongList {
    private lateinit var binding: MainNavigationDiscoverFragment1KugouBinding
    private val viewModel by lazyMy { ViewModelProvider(this).get(FragmentAKuGouViewModel::class.java) }

    private lateinit var mAdapter:CustomLengthRecyclerAdapter<NewSong.Data.Info>

    private var isLoadDataForTheFirstTime=true //第一次加载数据

    private var songList:ArrayList<SongLists> = ArrayList()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MainNavigationDiscoverFragment1KugouBinding.inflate(layoutInflater)

        viewModel.kuGouSpecialRecommendViewModel(1,25)
        return binding.root
    }

    @SuppressLint("Range")
    override fun lazyInit() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView2.layoutManager = layoutManager

        binding.refreshLayout.setEnableRefresh(false)   //禁用下拉刷新

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
        noInternetShowNewSongList()
    }
    private var page = 1
    @SuppressLint("WrongConstant")
    override fun haveInternetShowNewSongList() {
        mAdapter=NullAdapter(R.layout.main_navigation_discover_fragment1_item_b,20)
        binding.recyclerView2.adapter=mAdapter

               viewModel.observedLiveData.observe(this){
                   val newSong=it.getOrNull()
                   Timber.v("酷狗新歌推荐列表:%s", newSong)

                   var id= 0L
                     newSong?.data?.info?.forEach { info ->
                         val listFilename = info.filename!!.split("- ")
                         val picUrl = info.album_cover?.replace("{size}", "400")
                         info.apply {
                             songList.add(
                                 SongLists(
                                     id = ++id,
                                     SongName= listFilename[1],
                                     SingerName=listFilename[0],
                                     FileHash=hash!!,
                                     mixSongID=album_audio_id.toString(),
                                     lyrics = "",
                                     album = "",
                                     pic = picUrl!!,
                                     platform = NEW_SONG_KU_GOU
                                 )
                             )
                         }
                     }


                   if (isLoadDataForTheFirstTime){
                       isLoadDataForTheFirstTime=false

                       /*
                         存储数据
                       */
                       val gson = Gson().toJson(newSong?.data?.info)
                       setSp(context!!,"NewSongKuGou"){
                           putString("Info",gson)
                       }


                       viewModel.listLiveData.clear()
                       viewModel.listLiveData.addAll(newSong?.data?.info!!)
                       for (i in 0..4 ){
                           //每次都删除0索引
                           viewModel.listLiveData.removeAt(0)
                       }

                       binding.recyclerView2.adapter=null

                       mAdapter.notifyDataSetChanged()
                       mAdapter =object :CustomLengthRecyclerAdapter<NewSong.Data.Info> (viewModel.listLiveData,R.layout.main_navigation_discover_fragment1_item_b,20){
                           override fun onBindViewHolder(holder: SmartViewHolder?, model: NewSong.Data.Info?, position: Int) {
                               val img = model?.album_cover
                               img?.let { it1 -> holderImage(it1, "100", 20, holder!!, R.id.iv_main_fragment1_fragment_a_ku_gou_item) }
                               val listFilename = model?.filename?.split("- ")

                               holder?.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song, listFilename?.get(1))
                               holder?.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song_album, listFilename?.get(0))

                               songPlay(holder, position)
                           }

                       }
                       binding.recyclerView2.adapter=mAdapter

                   }

                   if (page >= 2) {
                       mAdapter.loadMore(newSong?.data?.info!!)
                       binding.refreshLayout.finishLoadMore()//完成加载更多
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
        val json = getSp(context!!, "NewSongKuGou") {
            getString("Info", "")
        }
        val gson = Gson()
        val typeOf = object : TypeToken<List<NewSong.Data.Info>>() {}.type
        val listInfo = gson.fromJson<List<NewSong.Data.Info>>(json, typeOf)

        viewModel.listLiveData.clear()
        viewModel.listLiveData.addAll(listInfo)
        for (i in 0..4 ){
            //每次都删除0索引
            viewModel.listLiveData.removeAt(0)
        }

        mAdapter =object :CustomLengthRecyclerAdapter<NewSong.Data.Info> (viewModel.listLiveData,R.layout.main_navigation_discover_fragment1_item_b,20){
            override fun onBindViewHolder(holder: SmartViewHolder?, model: NewSong.Data.Info?, position: Int) {
                val img = model?.album_cover
                img?.let { it1 -> holderImage(it1, "100", 20, holder!!, R.id.iv_main_fragment1_fragment_a_ku_gou_item) }
                val listFilename = model?.filename?.split("- ")

                holder?.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song, listFilename?.get(1))
                holder?.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song_album, listFilename?.get(0))

             songPlay(holder, position)
            }

        }
        binding.recyclerView2.adapter=mAdapter

        binding.refreshLayout.finishRefreshWithNoMoreData()  //标记没有更多数据
    }

    override fun songPlay(holder: SmartViewHolder?, position: Int) {
        holder?.itemView?.setOnClickListener {
            Timber.v("酷狗列表角标:%s 歌曲名称:%s", position, songList[position].SingerName)
            if(songList[position]== MusicServiceRemote.getCurrentSong()){
                val intent= Intent(activity, PlayActivity::class.java)
                intent.putExtra(MusicConstant.CURRENT_SONG,songList[position])  //向下一个Activity传入当前播放的歌曲
                activity?.startActivity(intent)
            }else{
                MusicServiceRemote.setPlayQueue(songList, IntentUtil.makeCodIntent(MusicConstant.PLAY_SELECTED_SONG).putExtra(MusicConstant.EXTRA_POSITION, position))

                val intent= Intent(activity, PlayActivity::class.java)
                intent.putExtra(MusicConstant.CURRENT_SONG,songList[position])
                // intent.putExtra("isPlay",false)  作废  2021.9.12 |14.32
                activity?.startActivity(intent)
            }
        }
    }
}