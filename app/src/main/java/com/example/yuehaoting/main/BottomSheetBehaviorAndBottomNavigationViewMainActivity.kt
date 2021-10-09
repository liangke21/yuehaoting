package com.example.yuehaoting.main

import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.yuehaoting.R
import com.example.yuehaoting.base.view.view.MusicButtonLayout
import com.example.yuehaoting.databinding.ActivityMainBinding
import com.example.yuehaoting.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * 作者: LiangKe
 * 时间: 2021/10/9 9:51
 * @property activity MainActivity
 * @property binding ActivityMainBinding
 * @property musicButton MusicButtonLayout
 * @constructor
 *
 * BottomNavigationView 控件的属性
 * BottomSheetBehavior 控件的属性
 */
class BottomSheetBehaviorAndBottomNavigationViewMainActivity(private val activity: MainActivity, private val binding: ActivityMainBinding) {
    private lateinit var musicButton: MusicButtonLayout

    init {
        initView()
    }


    private fun initView() {

        //底部导航栏
        val navView: BottomNavigationView = binding.layoutNavView.navView
        val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_discover, R.id.navigation_featured, R.id.navigation_list, R.id.navigation_my
            )
        )
        activity.setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //播放按钮
        musicButton = activity.findViewById(R.id.musicButton)

      val   behavior1= BottomSheetBehavior.from(activity.findViewById(R.id.bottom_sheet_behavior1))
      val   behavior2= BottomSheetBehavior.from(activity.findViewById(R.id.bottom_sheet_behavior2))




         //展开
        behavior1.state= BottomSheetBehavior.STATE_EXPANDED

        behavior2.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.e("onStateChanged",newState.toString())

                when(newState){
                    BottomSheetBehavior.STATE_COLLAPSED ->{
                        behavior1.state= BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        musicButton.setOnClickListener {
            behavior2.state = BottomSheetBehavior.STATE_EXPANDED
            behavior1.state= BottomSheetBehavior.STATE_COLLAPSED
        }

/*
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
*/
/*                    behavior1.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior2.state= BottomSheetBehavior.STATE_COLLAPSED*//*

                }
            }
           false
        }
*/

    }





}