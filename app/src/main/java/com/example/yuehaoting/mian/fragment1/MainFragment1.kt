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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.base.glide.GlideApp
import com.example.yuehaoting.base.magicIndicator.ext.CustomCommonNavigator
import com.example.yuehaoting.base.pageView.ViewPager2Helper
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.base.recyclerView.customLengthAdapter.CustomLengthRecyclerAdapter
import com.example.yuehaoting.base.recyclerView.customLengthAdapter.NullAdapter
import com.example.yuehaoting.data.kugou.specialRecommend.SetSpecialRecommend
import com.example.yuehaoting.data.kugou.specialRecommend.SpecialRecommend
import com.example.yuehaoting.databinding.MainFragment1Binding
import com.example.yuehaoting.kotlin.*
import com.example.yuehaoting.mian.fragment1.newSongRecommendationFragment.BaseFragmentNewSongRecommendation
import com.example.yuehaoting.mian.fragment1.newSongRecommendationFragment.Fragment1KuGou
import com.example.yuehaoting.mian.fragment1.newSongRecommendationFragment.Fragment1All
import com.example.yuehaoting.mian.fragment1.pageView.PageViewFragmentNewSongRecommendationAdapter
import com.example.yuehaoting.mian.viewModel.MainFragmentViewModel
import com.example.yuehaoting.util.NetworkUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
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

    private val mDataList: ArrayList<String> = ArrayList()

    private val fragmentList:ArrayList<BaseFragmentNewSongRecommendation> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MainFragment1Binding.inflate(inflater)
        viewModel.kuGouSpecialRecommendViewModel(pots())

        initData()
        initView()

        //是否打开网络
        val isNetWork = NetworkUtils.isNetWorkAvailable(context!!)
        if (isNetWork) {
            haveInternet()
        } else {
            noInternet()
        }
        initMagicIndicator()

        return binding.root
    }

    private fun initData() {
        mDataList.add("全部")
        mDataList.add("酷狗")
        mDataList.add("网易")
        mDataList.add("QQ")
        mDataList.add("酷我")
        mDataList.add("咪咕")

        fragmentList.add(Fragment1All())
        fragmentList.add(Fragment1KuGou())


    }

    private fun initView() {

        binding.vpMainFragment1.adapter = PageViewFragmentNewSongRecommendationAdapter(childFragmentManager, fragmentList, LifecycleRegistry(this).apply {
            currentState = Lifecycle.State.RESUMED
        })

        binding.vpMainFragment1.offscreenPageLimit =2

        binding.refreshLayout. setEnableLoadMore(false) // 关闭上拉加载更多

        val layoutManager = GridLayoutManager(context, 3)
        binding.recyclerView.layoutManager = layoutManager
/*
      binding.nestedScrollView.setOnScrollChangeListener(object :NestedScrollView.OnScrollChangeListener{

           override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
              Timber.v("滚动视图%s"," NestedScrollView: $v | scrollX: $scrollX | oldScrollX: $oldScrollX | oldScrollY: $oldScrollY")
           }
       })
        */
   }

   /**
    * 没有网络
    */
   private fun noInternet() {
       noInternetSpeciallyRecommendedPlaylistData()
   }

   /**
    * 有网络
    */
   private fun haveInternet() {
       haveInternetSpeciallyRecommendedPlaylistData()
   }

   /**
    * 没有网落
    * 获取特别预览的数据
    */
   private fun noInternetSpeciallyRecommendedPlaylistData() {
       val json = getSp(context!!, "SpecialRecommend") {
           getString("Special", "")
       }
       val gson = Gson()
       val typeOf = object : TypeToken<List<SpecialRecommend.Data.Special>>() {}.type
       val special: List<SpecialRecommend.Data.Special> = gson.fromJson(json, typeOf)

       Timber.v("观察到酷狗特别预览缓存数据数据%s", special[0].specialname)
       viewModel.listLiveData.clear()
       viewModel.listLiveData.addAll(special)
       mAdapter = object : CustomLengthRecyclerAdapter<SpecialRecommend.Data.Special>(viewModel.listLiveData, R.layout.main_fragment1_item_song_list, 6) {
           override fun onBindViewHolder(holder: SmartViewHolder, model: SpecialRecommend.Data.Special?, position: Int) {

               holderImage(holder, model)

               holder.text(R.id.tv_main_fragment1_item, model?.specialname)
           }

       }
       binding.recyclerView.adapter = mAdapter

   }

   /**
    * 有网落
    * 获取特别预览的数据
    */
   @SuppressLint("VisibleForTests")
   private fun haveInternetSpeciallyRecommendedPlaylistData() {
       mAdapter = NullAdapter(R.layout.main_fragment1_item_song_list, 6)
       binding.recyclerView.adapter = mAdapter
       viewModel.observedLiveData.observe(this) {
           tryNull {
               val mSpecialRecommend = it.getOrNull() as SpecialRecommend
               val gson = Gson().toJson(mSpecialRecommend.data?.special_list)
               setSp(context!!, "SpecialRecommend") {
                   putString("Special", gson)
               }
               Timber.v("观察到酷狗特别预览数据%s", mSpecialRecommend.data?.special_list?.get(0)?.specialname)
               viewModel.listLiveData.clear()

               viewModel.listLiveData.addAll(mSpecialRecommend.data?.special_list!!)
               binding.recyclerView.adapter = null
               mAdapter.notifyDataSetChanged()

               mAdapter = object : CustomLengthRecyclerAdapter<SpecialRecommend.Data.Special>(viewModel.listLiveData, R.layout.main_fragment1_item_song_list, 6) {
                   override fun onBindViewHolder(holder: SmartViewHolder, model: SpecialRecommend.Data.Special?, position: Int) {

                       holderImage(holder, model)

                       holder.text(R.id.tv_main_fragment1_item, model?.specialname)
                   }

               }
               binding.recyclerView.adapter = mAdapter

           }

       }

   }


   @SuppressLint("CheckResult")
   private fun holderImage(holder: SmartViewHolder, model: SpecialRecommend.Data.Special?) {
       //图片圆角
       val requestOptions = RequestOptions()
       // requestOptions.placeholder(R.drawable.ic_launcher_background)
       RequestOptions.circleCropTransform()
       requestOptions.transform(RoundedCorners(30))

       val img = model?.imgurl
       val picUrl = img?.replace("{size}", "400")

       GlideApp.with(this).asBitmap()
           .apply(requestOptions)
           .load(picUrl)
           .placeholder(R.drawable.load_started)
           .into(object : CustomTarget<Bitmap>() {
               override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                   holder.image(R.id.iv_main_fragment1_item, resource)
               }

               override fun onLoadCleared(placeholder: Drawable?) {
                   holder.image(R.id.iv_main_fragment1_item, R.drawable.load_cleared)
               }

               override fun onLoadStarted(placeholder: Drawable?) {
                   super.onLoadStarted(placeholder)
                   holder.image(R.id.iv_main_fragment1_item, placeholder)
               }
           })

   }

   /**
    * 新歌推荐标题栏
    */
   private fun initMagicIndicator() {
       val magicIndicator = binding.miMainFragment1NewSongRecommendation
       // magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg);
       val commonNavigator = CustomCommonNavigator(context)
       commonNavigator.isAdjustMode=true  //标题自适应
       commonNavigator.scrollPivotX = 0.65f
       commonNavigator.adapter = object : CommonNavigatorAdapter() {
           override fun getCount(): Int {
               return mDataList.size
           }

           override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
               val clipPagerTitleView = SimplePagerTitleView(
                   context!!
               )
               Timber.v("clipPagerTitleView%s",mDataList[index])
               clipPagerTitleView.text = mDataList[index]
               clipPagerTitleView.textSize = 14f
               clipPagerTitleView.gravity = Gravity.CENTER
               clipPagerTitleView.normalColor = Color.parseColor("#323131")
               clipPagerTitleView.selectedColor = Color.WHITE
               clipPagerTitleView.setOnClickListener { binding.vpMainFragment1.currentItem = index }
               return clipPagerTitleView
           }

           override fun getIndicator(context: Context): IPagerIndicator {
               val indicator = LinePagerIndicator(context)
               val navigatorHeight = 70f
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
      ViewPager2Helper.bind(magicIndicator, binding.vpMainFragment1, childFragmentManager,activity?.intent)

     //binding.vpMainFragment1.requestLayout()
   }

   /**
    * 特别推荐歌单 请求头
    */
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