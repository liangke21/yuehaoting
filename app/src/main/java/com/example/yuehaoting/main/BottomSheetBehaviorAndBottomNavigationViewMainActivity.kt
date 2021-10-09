package com.example.yuehaoting.main

import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.BaseActivity
import com.example.yuehaoting.base.view.view.MusicButtonLayout
import com.example.yuehaoting.callback.MusicEvenCallback
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.ActivityMainBinding
import com.example.yuehaoting.musicService.service.MusicService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import timber.log.Timber

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
class BottomSheetBehaviorAndBottomNavigationViewMainActivity(private val activity: MainActivity, private val binding: ActivityMainBinding) : MusicEvenCallback {
    private lateinit var musicButton: MusicButtonLayout

    private var baseActivity: BaseActivity = activity

    init {

        initView()
    }


    private fun initView() {
        baseActivity.addMusicServiceEventListener(this)

        //底部导航栏
        val navView: BottomNavigationView = binding.layoutNavView.navView
        val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        //播放按钮
        musicButton = activity.findViewById(R.id.musicButton)

        val behavior1 = BottomSheetBehavior.from(activity.findViewById(R.id.bottom_sheet_behavior1))
        val behavior2 = BottomSheetBehavior.from(activity.findViewById(R.id.bottom_sheet_behavior2))

        //展开
        behavior1.state = BottomSheetBehavior.STATE_EXPANDED

        behavior2.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.e("onStateChanged", newState.toString())

                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        behavior1.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        musicButton.setOnClickListener {
            behavior2.state = BottomSheetBehavior.STATE_EXPANDED
            behavior1.state = BottomSheetBehavior.STATE_COLLAPSED
        }

    }


    override fun onMediaStoreChanged() {

    }

    override fun onPermissionChanged(has: Boolean) {

    }

    override fun onPlayListChanged(name: String) {

    }

    override fun onServiceConnected(service: MusicService) {
        Timber.v("BottomSheetBehaviorAndBottomNavigationViewMainActivity绑定")
    }

    override fun onMetaChanged() {

    }

    override fun onPlayStateChange() {

    }

    override fun onServiceDisConnected() {

    }

    override fun onTagChanged(oldSong: SongLists, newSongLists: SongLists) {

    }

    fun onDestroy() {
        baseActivity.removeMusicServiceEventListener(this)
    }
}