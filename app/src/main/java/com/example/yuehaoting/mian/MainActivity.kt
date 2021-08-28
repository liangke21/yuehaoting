package com.example.yuehaoting.mian

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.example.yuehaoting.base.fragmet.LazyBaseFragment
import com.example.yuehaoting.databinding.ActivityMainBinding
import com.example.yuehaoting.searchFor.fragment.ext.MyCommonNavigator
import com.example.yuehaoting.searchFor.fragment.ext.ScaleTransitionPagerTitleView
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mTitleList:ArrayList<String> = ArrayList()

    private var fragmentList = ArrayList<LazyBaseFragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
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

       fragmentList.add()
    }

    /**
     * 初始化控件颜色
     */
    private fun initViewColors() {

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
                val simplePagerTitleView: SimplePagerTitleView =
                    ScaleTransitionPagerTitleView(
                        context
                    )
                simplePagerTitleView.text = mTitleList[index]
                simplePagerTitleView.textSize = 18F
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