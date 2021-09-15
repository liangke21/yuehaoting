package com.example.yuehaoting.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.drawerlayout.widget.DrawerLayout


import com.example.yuehaoting.App.Companion.context
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.BaseActivity
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.base.magicIndicator.MySimplePagerTitleView
import com.example.yuehaoting.base.magicIndicator.ext.MyCommonNavigator
import com.example.yuehaoting.databinding.ActivityMainBinding
import com.example.yuehaoting.main.fragment1.MainFragment1
import com.example.yuehaoting.main.fragment2.MainFragment2
import com.example.yuehaoting.main.pageView.PageViewFragmentMainAdapter
import com.example.yuehaoting.searchFor.SearchActivity
import com.example.yuehaoting.theme.Theme
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import timber.log.Timber
import kotlin.math.abs


class MainActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    private val mTitleList: ArrayList<String> = ArrayList()

    private var fragmentList = ArrayList<BaseFragment>()
    private var isDrawer :Boolean =true
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
        fragmentList.add(MainFragment2())
        binding.vpMainContent.adapter = PageViewFragmentMainAdapter(supportFragmentManager, fragmentList)
        binding.vpMainContent.offscreenPageLimit = 2
    }

    /**
     * 初始化控件颜色
     */
    private fun initViewColors() {
        // binding.btMainSearch.setBackgroundColor(Color.parseColor("#1C1C1C"))
        binding.btMainSearch.setImageDrawable(Theme.tintDrawable(context.resources.getDrawable(R.drawable.search_item_tv, null), Color.parseColor("#1C1C1C")))
        binding.btMainNavigation.setImageDrawable(Theme.tintDrawable(context.resources.getDrawable(R.drawable.main_navigation, null), Color.parseColor("#1C1C1C")))
    }

    /**
     * 初始化控件
     */
    private fun initView() {
        binding.btMainSearch.setOnClickListener(this)
        binding.btMainNavigation.setOnClickListener(this)

        val layout= DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT,DrawerLayout.LayoutParams.MATCH_PARENT)
         layout.gravity=Gravity.START
        binding.llMainLeft.layoutParams=layout

      binding.drawer.addDrawerListener(object :DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                isDrawer=false
            }

            override fun onDrawerClosed(drawerView: View) {
               isDrawer=true
            }

            override fun onDrawerStateChanged(newState: Int) {
                Timber.v("onDrawerStateChanged %s",newState)

            }
        })

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
        ViewPagerHelper.bind(magicIndicator, binding.vpMainContent)
    }

    /**
     * 适配fragment
     */
    private fun initPageViewFragment() {

    }

    @SuppressLint("RtlHardcoded")
    override fun onClick(v: View) {
        when (v.id) {
            binding.btMainSearch.id -> {
                val intent = Intent(this, SearchActivity::class.java)

                startActivity(intent)

            }

            binding.btMainNavigation.id->{
                Timber.v("openDrawer%s",Gravity.RIGHT)
                binding.drawer.openDrawer(Gravity.RIGHT)
            }
        }
    }

    //手指上下滑动时的最小速度
    private val minIMumSpeed = 1000

    //手指向右滑动时的最小距离
    private val shortestDistance = 50

    //手指向上滑或下滑时的最小距离
    private val minimumDistanceToSlide = 100

    //记录手指按下时的横坐标。
    private var xDown = 0f

    //记录手指按下时的纵坐标。
    private var yDown = 0f

    //记录手指移动时的横坐标。
    private var xMove = 0f

    //记录手指移动时的纵坐标。
    private var yMove = 0f

    //用于计算手指滑动的速度。
    private var mVelocityTracker: VelocityTracker? = null

    private var distanceX = 0
    private var distanceY = 0
    private var ySpeed = 0

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        createVelocityTracker(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xDown = event.rawX
                yDown = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                xMove = event.rawX
                yMove = event.rawY
                //滑动的距离
                distanceX = (xMove - xDown).toInt()
                distanceY = (yMove - yDown).toInt()
                //获取顺时速度
                ySpeed = getScrollVelocity()
            }
            MotionEvent.ACTION_UP -> {
                recycleVelocityTracker()
                //关闭Activity需满足以下条件：
                //1.x轴滑动的距离>XDISTANCE_MIN
                //2.y轴滑动的距离在YDISTANCE_MIN范围内
                //3.y轴上（即上下滑动的速度）<XSPEED_MIN，如果大于，则认为用户意图是在上下滑动而非左滑结束Activity
               val fragment= intent.getIntExtra("fragment",1)
                if (fragment==0 && isDrawer){
                    if (distanceX > shortestDistance && distanceY < minimumDistanceToSlide && distanceY > -minimumDistanceToSlide && ySpeed < minIMumSpeed) {
                        Timber.v("openDrawer%s", distanceX )
                        binding.drawer.openDrawer(binding.llMainLeft)
                        return false
                    }
                }

            }
        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * 创建VelocityTracker对象，并将触摸界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event 触摸滑动事件
     */
    private fun createVelocityTracker(event: MotionEvent) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
    }

    /**
     * 回收VelocityTracker对象。
     */
    private fun recycleVelocityTracker() {
        mVelocityTracker!!.recycle()
        mVelocityTracker = null
    }

    /**
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private fun getScrollVelocity(): Int {
        mVelocityTracker!!.computeCurrentVelocity(1000)
        val velocity = mVelocityTracker!!.yVelocity.toInt()
        return abs(velocity)
    }

}