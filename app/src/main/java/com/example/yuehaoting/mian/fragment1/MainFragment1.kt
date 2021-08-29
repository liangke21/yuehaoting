package com.example.yuehaoting.mian.fragment1

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.base.recyclerView.adapter.CustomLengthRecyclerAdapter
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.data.kugou.specialRecommend.SetSpecialRecommend
import com.example.yuehaoting.data.kugou.specialRecommend.SpecialRecommend
import com.example.yuehaoting.databinding.MainFragment1Binding
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.mian.viewModel.MainFragmentViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


/**
 * 作者: 天使
 * 时间: 2021/8/28 16:25
 * 描述:
 */
class MainFragment1 : BaseFragment() {
    private lateinit var binding: MainFragment1Binding

    private var viewModel by lazyMy { ViewModelProvider(this).get(MainFragmentViewModel::class.java) }

    private lateinit var mAdapter: CustomLengthRecyclerAdapter<SpecialRecommend.Data.Special>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MainFragment1Binding.inflate(inflater)

        viewModel.kuGouSpecialRecommendViewModel(pots())
        return binding.root
    }

    override fun onResume() {
        super.onResume()

       val layoutManager = GridLayoutManager(context, 3)
       // val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager


        viewModel.observedLiveData.observe(this) {
            tryNull {
                val mSpecialRecommend = it.getOrNull() as SpecialRecommend
                Timber.v("观察到酷狗特别预览数据%s",mSpecialRecommend.data?.special_list?.get(0)?.specialname)
                viewModel.listLiveData.clear()

                viewModel.listLiveData.addAll(mSpecialRecommend.data?.special_list!!)

                mAdapter = object : CustomLengthRecyclerAdapter<SpecialRecommend.Data.Special> (viewModel.listLiveData, R.layout.main_fragment1_item_song_list){
                    override fun onBindViewHolder(holder: SmartViewHolder, model: SpecialRecommend.Data.Special?, position: Int) {

                        holderImage(holder,model,position)

                        holder.text(R.id.tv_main_fragment1_item,model?.specialname)
                    }

                }

                binding.recyclerView.adapter=mAdapter
            }


        }

    }

    @SuppressLint("CheckResult")
    private fun holderImage(holder: SmartViewHolder, model: SpecialRecommend.Data.Special?, position: Int) {
        //图片圆角
        val requestOptions = RequestOptions()
        // requestOptions.placeholder(R.drawable.ic_launcher_background)
        RequestOptions.circleCropTransform()
        requestOptions.transform(RoundedCorners(30))

            val img= model?.imgurl
            val picUrl=  img?.replace("{size}","400")

        Glide.with(this).asBitmap()
            .apply(requestOptions)
            .load(picUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    holder.image(R.id.iv_main_fragment1_item,resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })


    }

    private fun pots(): SetSpecialRecommend {

        val json = "{\n" +
                "    \"apiver\": 3,\n" +
                "    \"appid\": 1005,\n" +
                "    \"client_playlist\": [],\n" +
                "    \"client_playlist_flag\": 0,\n" +
                "    \"clienttime\": 1629988069,\n" +
                "    \"clientver\": 10589,\n" +
                "    \"key\": \"4a9c47b9ed3868d0cdef6794798e20d3\",\n" +
                "    \"mid\": \"95751731536823398275799557653797333100\",\n" +
                "    \"module_id\": 5,\n" +
                "    \"platform\": \"android\",\n" +
                "    \"req_multi\": 1,\n" +
                "    \"session\": \"\",\n" +
                "    \"special_list\": [\n" +
                "        {\n" +
                "            \"A\": 1,\n" +
                "            \"F\": 1,\n" +
                "            \"ID\": 0,\n" +
                "            \"T\": 0\n" +
                "        }\n" +
                "    ],\n" +
                "    \"theme_last_showtime\": 0,\n" +
                "    \"userid\": \"0\"\n" +
                "}"

        val gson = Gson()

        val typeOf = object : TypeToken<SetSpecialRecommend>() {}.type

        return gson.fromJson(json, typeOf)
    }

}