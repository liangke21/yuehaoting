package com.example.yuehaoting.mian

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.yuehaoting.App.Companion.context
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.base.magicIndicator.MySimplePagerTitleView
import com.example.yuehaoting.base.magicIndicator.ext.MyCommonNavigator
import com.example.yuehaoting.databinding.ActivityMainBinding
import com.example.yuehaoting.mian.fragment1.MainFragment1
import com.example.yuehaoting.mian.pageView.PageViewFragmentAdapter
import com.example.yuehaoting.theme.Theme
import com.example.yuehaoting.util.NetworkUtils
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mTitleList: ArrayList<String> = ArrayList()

    private var fragmentList = ArrayList<BaseFragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewColors()
        initData()
        initView()

        initMagicIndicator()
        initPageViewFragment()





    }


    /**
     * 初始化数据
     */
    private fun initData() {
        mTitleList.add("推荐")
        mTitleList.add("视频")
        mTitleList.add("听书")

        fragmentList.add(MainFragment1())
        binding.vpMainContent.adapter = PageViewFragmentAdapter(supportFragmentManager, fragmentList)
        binding.vpMainContent.offscreenPageLimit = 1
    }

    /**
     * 初始化控件颜色
     */
    private fun initViewColors() {
       // binding.btMainSearch.setBackgroundColor(Color.parseColor("#1C1C1C"))
        binding.btMainSearch.setImageDrawable(Theme.tintDrawable(context.resources.getDrawable(R.drawable.search_item_tv,null), Color.parseColor("#1C1C1C")))
        binding.btMainNavigation.setImageDrawable(Theme.tintDrawable(context.resources.getDrawable(R.drawable.main_navigation,null), Color.parseColor("#1C1C1C")))
    }

    /**
     * 初始化控件
     */
    private fun initView() {

    }

    /**
     * 标题栏
     */
    private fun initMagicIndicator() {
        val magicIndicator = binding.miMainTitle
        magicIndicator.setBackgroundColor(Color.parseColor("#fafafa"))
        val commonNavigator7 = MyCommonNavigator(this)

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
                simplePagerTitleView.gravity=Gravity.BOTTOM
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
        ViewPagerHelper.bind(magicIndicator, binding.vpMainContent)
    }

    /**
     * 适配fragment
     */
    private fun initPageViewFragment() {

    }
}