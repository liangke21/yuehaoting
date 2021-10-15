package com.example.yuehaoting.main.ui.discover

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.yuehaoting.App
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.MyFragment
import com.example.yuehaoting.base.magicIndicator.MySimplePagerTitleView
import com.example.yuehaoting.base.magicIndicator.ext.MyCommonNavigator
import com.example.yuehaoting.base.pageView.ViewPageHelperDiscover
import com.example.yuehaoting.databinding.MainNavigationDiscoverBinding
import com.example.yuehaoting.main.discover.fragment1.MainFragment1
import com.example.yuehaoting.main.discover.fragment2.MainFragment2
import com.example.yuehaoting.main.pageView.PageViewFragmentMainAdapter
import com.example.yuehaoting.searchFor.SearchActivity
import com.example.yuehaoting.theme.Theme
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class DiscoverFragment : MyFragment(), View.OnClickListener {

    private lateinit var homeViewModel: DiscoverViewModel
    private var _binding: MainNavigationDiscoverBinding? = null


    private val binding get() = _binding!!


    private val mTitleList: ArrayList<String> = ArrayList()

    private var fragmentList = ArrayList<Fragment>()

    private lateinit var mCallbackActivity: CallbackActivity


    /*    private val viewModelDiscoverFragment by lazyMy {
            ViewModelProvider(activity!!).get(DiscoverViewModel::class.java)
        }*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.e("DiscoverFragment", "nCreateView")
       homeViewModel = ViewModelProvider(this).get(DiscoverViewModel::class.java)
        _binding = MainNavigationDiscoverBinding.inflate(inflater)
        initViewColors()
        initData()


      initMagicIndicator()
       initView()
       mCallbackActivity = activity as CallbackActivity


        return binding.root
    }


    /**
     * 初始化数据
     */
    private fun initData() {
        mTitleList.add("推荐")
        mTitleList.add("视频")
        mTitleList.add("听书")

       fragmentList.add(MainFragment1())
        fragmentList.add(MainFragment2())
        binding.vpMainContent.adapter = PageViewFragmentMainAdapter(childFragmentManager, fragmentList)
        binding.vpMainContent.offscreenPageLimit = 1
    }

    /**
     * 初始化控件颜色
     */
    private fun initViewColors() {
        // binding.btMainSearch.setBackgroundColor(Color.parseColor("#1C1C1C"))
        binding.btMainSearch.setImageDrawable(Theme.tintDrawable(App.context.resources.getDrawable(R.drawable.search_item_tv, null), Color.parseColor("#1C1C1C")))
        binding.btMainNavigation.setImageDrawable(Theme.tintDrawable(App.context.resources.getDrawable(R.drawable.main_navigation, null), Color.parseColor("#1C1C1C")))
    }

    /**
     * 初始化控件
     */
    private fun initView() {
        binding.btMainSearch.setOnClickListener(this)
        binding.btMainNavigation.setOnClickListener(this)

    }

    /**
     * 标题栏
     */
    private fun initMagicIndicator() {
        val magicIndicator = binding.miMainTitle
        magicIndicator.setBackgroundColor(Color.parseColor("#fafafa"))
        val commonNavigator7 = MyCommonNavigator(context)

        commonNavigator7.scrollPivotX = 0.65f
        commonNavigator7.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mTitleList.size
            }

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val simplePagerTitleView: MySimplePagerTitleView =
                    MySimplePagerTitleView(
                        context!!
                    )
                simplePagerTitleView.text = mTitleList[index]
                simplePagerTitleView.textSize = 16F
                simplePagerTitleView.gravity = Gravity.BOTTOM
                simplePagerTitleView.normalColor = Color.parseColor("#1C1C1C")
                simplePagerTitleView.selectedColor = Color.parseColor("#000000")
                simplePagerTitleView.setOnClickListener { binding.vpMainContent.currentItem = index }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY

                indicator.lineHeight = UIUtil.dip2px(context, 6.0).toFloat()
                indicator.lineWidth = UIUtil.dip2px(context, 10.0).toFloat()
                indicator.roundRadius = UIUtil.dip2px(context, 3.0).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(Color.parseColor("#00c853"))
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator7
       ViewPageHelperDiscover.bind(magicIndicator, binding.vpMainContent, activity!!.intent)
    }


    @SuppressLint("RtlHardcoded")
    override fun onClick(v: View) {
        when (v.id) {
            binding.btMainSearch.id -> {
                val intent = Intent(activity, SearchActivity::class.java)
                startActivity(intent)

            }

            binding.btMainNavigation.id -> {
                mCallbackActivity.activityMonitoringFragment()
            }
        }
    }

    interface CallbackActivity {
        fun activityMonitoringFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vpMainContent.adapter = null
        mTitleList.clear()
        fragmentList.clear()
        _binding = null
    }
}