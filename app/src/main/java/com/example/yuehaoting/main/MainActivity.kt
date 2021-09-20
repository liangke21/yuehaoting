package com.example.yuehaoting.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.yuehaoting.base.view.MusicButtonLayout
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.BaseActivity
import com.example.yuehaoting.databinding.ActivityMainBinding
import com.example.yuehaoting.main.ui.discover.DiscoverFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber
import java.lang.StrictMath.abs


class MainActivity : BaseActivity() ,DiscoverFragment.CallbackActivity{
    private lateinit var binding: ActivityMainBinding

    private lateinit var musicButton: MusicButtonLayout
    private var isDrawer: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //去除BottomNavigationView标题栏
        this.supportActionBar?.hide()
        initView()
    }

    private fun initView() {

        //两侧导航栏
        val layout = DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT)
        layout.gravity = Gravity.START
        binding.llMainLeft.layoutParams = layout
        //左右侧导航栏
        binding.drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                isDrawer = false
            }

            override fun onDrawerClosed(drawerView: View) {
                isDrawer = true
            }

            override fun onDrawerStateChanged(newState: Int) {
                Timber.v("onDrawerStateChanged %s", newState)

            }
        })

        //底部导航栏
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_discover, R.id.navigation_featured, R.id.navigation_list, R.id.navigation_my
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //播放按钮
        musicButton = findViewById(R.id.musicButton)
        binding.rlMain.bringChildToFront(musicButton)

        musicButton.setTotalProgress(300)
        musicButton.playMusic()
        musicButton.setOnClickListener {
            musicButton.playMusic()
        }
    }

    /**
     * 监听fragment传过来的监听事件
     */
    @SuppressLint("RtlHardcoded")
    override fun activityMonitoringFragment() {
       Timber.v("你瞅啥","瞅你咋地")
        Timber.v("openDrawer%s", Gravity.RIGHT)
        binding.drawer.openDrawer(Gravity.RIGHT)
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
                val fragment = intent.getIntExtra("fragment", 1)
                val discover = intent.getIntExtra("discover",1)
                if (fragment == 0 && isDrawer && discover==0) {
                    if (distanceX > shortestDistance && distanceY < minimumDistanceToSlide && distanceY > -minimumDistanceToSlide && ySpeed < minIMumSpeed) {
                        Timber.v("openDrawer%s", distanceX)
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



