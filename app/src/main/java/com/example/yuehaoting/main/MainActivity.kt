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
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.yuehaoting.base.view.MusicButtonLayout


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
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import timber.log.Timber
import kotlin.math.abs


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var musicButton: MusicButtonLayout
    private var isDrawer: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // this.supportActionBar?.hide();
       // initViewColors()
      //  initData()


     //   initMagicIndicator()

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
        musicButton = findViewById(R.id.musicButton)
        binding.rlMain.bringChildToFront(musicButton)

        musicButton.setTotalProgress(300)
        musicButton.playMusic()
        musicButton.setOnClickListener {
            musicButton.playMusic()
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_discover, R.id.navigation_featured, R.id.navigation_list,R.id.navigation_my
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }




    }



