package com.example.yuehaoting.mian.fragment1

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.base.magicIndicator.MySimplePagerTitleView
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
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import timber.log.Timber


/**
 * 作者: 天使
 * 时间: 2021/8/28 16:25
 * 描述:
 */
class MainFragment1 : BaseFragment() {
    private lateinit var binding: MainFragment1Binding

    private var viewModel by lazyMy { ViewModelProvider(this).get(MainFragmentViewModel::class.java) }

    private lateinit var mAdapter: CustomLengthRecyclerAdapter<SpecialRecommend.Data.Special>

    private val mDataList:ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MainFragment1Binding.inflate(inflater)
        viewModel.kuGouSpecialRecommendViewModel(pots())
        initData()
        initMagicIndicator()
        return binding.root
    }

    private fun initData() {
        mDataList.add("全部")
        mDataList.add("酷狗")
        mDataList.add("网易")
        mDataList.add("qq")
        mDataList.add("酷我")
        mDataList.add("咪咕")
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

                mAdapter = object : CustomLengthRecyclerAdapter<SpecialRecommend.Data.Special> (viewModel.listLiveData, R.layout.main_fragment1_item_song_list ){
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
    private fun initMagicIndicator() {
        val magicIndicator =binding.miMainFragment1NewSongRecommendation
        // magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg);
        val commonNavigator = CommonNavigator(context)
        commonNavigator.scrollPivotX = 0.65f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mDataList.size
            }

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView? {
                val clipPagerTitleView = SimplePagerTitleView(
                    context!!
                )
                clipPagerTitleView.text = mDataList[index]
                clipPagerTitleView.textSize=14f
                clipPagerTitleView.gravity=Gravity.CENTER
                clipPagerTitleView.normalColor = Color.parseColor("#323131")
                clipPagerTitleView.selectedColor = Color.WHITE
                clipPagerTitleView.setOnClickListener { binding.vpMainFragment1.currentItem = index }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                val indicator = LinePagerIndicator(context)
                val navigatorHeight =70f
                val borderWidth: Int = UIUtil.dip2px(context, 1.0)
                val lineHeight = navigatorHeight - 2 * borderWidth
                indicator.lineHeight = lineHeight
                indicator.roundRadius = lineHeight / 2
                indicator.yOffset = borderWidth.toFloat()
                indicator.setColors(Color.parseColor("#ACA7A7"))
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.vpMainFragment1)
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