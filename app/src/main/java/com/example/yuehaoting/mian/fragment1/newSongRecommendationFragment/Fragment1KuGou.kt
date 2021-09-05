package com.example.yuehaoting.mian.fragment1.newSongRecommendationFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yuehaoting.R
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.base.recyclerView.customLengthAdapter.CustomLengthRecyclerAdapter
import com.example.yuehaoting.data.kugou.NewSong
import com.example.yuehaoting.data.kugou.specialRecommend.SpecialRecommend
import com.example.yuehaoting.databinding.MainFragment1FragmentAKugouBinding
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.mian.fragment1.viewModel.FragmentAKuGouViewModel
import com.example.yuehaoting.util.NetworkUtils
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/4 18:13
 * 描述:
 */
class Fragment1KuGou : BaseFragmentNewSongRecommendation(), ShowNewSongList {
    private lateinit var binding: MainFragment1FragmentAKugouBinding

    private var viewModel by lazyMy { ViewModelProvider(this).get(FragmentAKuGouViewModel::class.java) }

    private lateinit var mAdapter: CustomLengthRecyclerAdapter<NewSong.Data.Info>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MainFragment1FragmentAKugouBinding.inflate(layoutInflater)

        viewModel.kuGouSpecialRecommendViewModel("请求")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerview.layoutManager = layoutManager

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
        TODO("Not yet implemented")
    }

    override fun haveInternetShowNewSongList() {
        viewModel.observedLiveData.observe(this) {
            val newSong = it.getOrNull()
            Timber.v("酷狗新歌推荐%s", newSong.toString())
            viewModel.listLiveData.clear()
            viewModel.listLiveData.addAll(newSong?.data?.info!!)
          mAdapter=object :CustomLengthRecyclerAdapter<NewSong.Data.Info>(newSong.data.info, R.layout.main_fragment1_fragment_item,5){

              override fun onBindViewHolder(holder: SmartViewHolder?, model: NewSong.Data.Info?, position: Int) {
                    val img= model?.album_cover
                   holderImage(img!!,"100",20,holder!!,R.id.iv_main_fragment1_fragment_a_ku_gou_item)
                  val listFilename= model.filename?.split("- ")

                  holder.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song,listFilename?.get(1))
                  holder.text(R.id.tv_main_fragment1_fragment_a_ku_gou_item_song_album,listFilename?.get(0))
              }
          }

            binding.recyclerview.adapter=mAdapter
        }
    }

    override fun noInternetShowNewSongList() {
        TODO("Not yet implemented")
    }


}