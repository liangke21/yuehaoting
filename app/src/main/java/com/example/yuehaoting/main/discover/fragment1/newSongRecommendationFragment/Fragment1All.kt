package com.example.yuehaoting.main.discover.fragment1.newSongRecommendationFragment

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
import com.example.yuehaoting.databinding.MainNavigationDiscoverFragment1QuanbuBinding
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.kotlin.setSp
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.main.discover.fragment1.viewModel.FragmentAKuGouViewModel
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.playInterface.activity.PlayActivity
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.NetworkUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/4 18:13
 * 描述:
 */
class Fragment1All : BaseFragmentNewSongRecommendation(), ShowNewSongList {
    private lateinit var binding: MainNavigationDiscoverFragment1QuanbuBinding

    private var viewModel by lazyMy { ViewModelProvider(this).get(FragmentAKuGouViewModel::class.java) }

    private lateinit var mAdapter: CustomLengthRecyclerAdapter<NewSong.Data.Info>

    private var songList:ArrayList<SongLists> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MainNavigationDiscoverFragment1QuanbuBinding.inflate(layoutInflater)

        viewModel.kuGouSpecialRecommendViewModel(1,5)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
       // binding.root.requestLayout()
    }
    override fun lazyInit() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerview.layoutManager = layoutManager

       binding.refreshLayout.setEnableRefresh(false)   //禁用下拉刷新

        val isNetWork = NetworkUtils.isNetWorkAvailable(context!!)
        if (isNetWork) {
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

    override fun haveInternetShowNewSongList() {
        mAdapter=NullAdapter(R.layout.main_navigation_discover_fragment1_item_b, 5)
        binding.recyclerview.adapter=mAdapter
        tryNull {


        viewModel.observedLiveData.observe(this) { it ->
            val newSong = it.getOrNull()
            Timber.v("酷狗新歌推荐%s", newSong.toString())

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
                            platform = MusicConstant.NEW_SONG_KU_GOU
                        )
                    )
                }
            }


            val gson = Gson().toJson(newSong?.data?.info)
            setSp(context!!, "NewSong") {
                putString("Info", gson)
            }
            viewModel.listLiveData.clear()
            newSong?.data?.info?.let { info->
                viewModel.listLiveData.addAll(info)
            }


            binding.recyclerview.adapter = null
            mAdapter.notifyDataSetChanged()

            mAdapter = object : CustomLengthRecyclerAdapter<NewSong.Data.Info>(viewModel.listLiveData, R.layout.main_navigation_discover_fragment1_item_b, 5) {

                override fun onBindViewHolder(holder: SmartViewHolder?, model: NewSong.Data.Info?, position: Int) {
                    val img = model?.album_cover
                    holder?.let { it1 ->
                        img?.let { it2 ->
                            holderImage(
                                it2, "100", 20,
                                it1, R.id.iv_main_fragment1_fragment_a_ku_gou_item)
                        }

                        val listFilename = model?.filename?.split("- ")

                        it1.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song, listFilename?.get(1))
                        it1.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song_album, listFilename?.get(0))
                    }

                    songPlay(holder, position)
                }
            }

            binding.recyclerview.adapter = mAdapter
        }
        }
    }

    override fun noInternetShowNewSongList() {
        val json = getSp(context!!, "NewSong") {
            getString("Info", "")
        }
        val gson = Gson()
        val typeOf = object : TypeToken<List<NewSong.Data.Info>>() {}.type
        val listInfo = gson.fromJson<List<NewSong.Data.Info>>(json, typeOf)

        viewModel.listLiveData.clear()
        viewModel.listLiveData.addAll(listInfo)



        mAdapter = object : CustomLengthRecyclerAdapter<NewSong.Data.Info>(listInfo, R.layout.main_navigation_discover_fragment1_item_b, 5) {

            override fun onBindViewHolder(holder: SmartViewHolder?, model: NewSong.Data.Info?, position: Int) {
                val img = model?.album_cover
                holderImage(img!!, "100", 20, holder!!, R.id.iv_main_fragment1_fragment_a_ku_gou_item)
                val listFilename = model.filename?.split("- ")

                holder.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song, listFilename?.get(1))
                holder.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song_album, listFilename?.get(0))
                songPlay(holder, position)
            }
        }

        binding.recyclerview.adapter = mAdapter
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