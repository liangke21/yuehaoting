package com.example.yuehaoting.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.BaseActivity
import com.example.yuehaoting.base.activity.SmMainActivity
import com.example.yuehaoting.base.view.view.MusicButtonLayout
import com.example.yuehaoting.data.kugou.specialRecommend.SetSpecialRecommend
import com.example.yuehaoting.databinding.ActivityMainBinding
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.main.discover.fragment1.MainFragment1
import com.example.yuehaoting.main.discover.fragment1.viewModel.FragmentAKuGouViewModel
import com.example.yuehaoting.main.discover.fragment2.MainFragment2
import com.example.yuehaoting.main.ui.discover.DiscoverFragment
import com.example.yuehaoting.main.ui.discover.DiscoverViewModel
import com.example.yuehaoting.main.viewModel.MainFragmentViewModel
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.searchFor.SearchActivity
import com.example.yuehaoting.util.MyUtil.getSecond
import com.example.yuehaoting.util.StatusBarUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import timber.log.Timber
import java.lang.StrictMath.abs



class MainActivity : BaseActivity(), DiscoverFragment.CallbackActivity {
    private lateinit var binding: ActivityMainBinding

    private lateinit var musicButton: MusicButtonLayout

    private var isDrawer: Boolean = true

  //  private lateinit var bb: BottomSheetBehaviorMainActivity

    /**
     * ??????????????????
     */
    private var isPlaying = false

    private var viewModelMainFragment1 by lazyMy { ViewModelProvider(this).get(MainFragmentViewModel::class.java) }

    private var viewModelFragment1All by lazyMy { ViewModelProvider(this).get(FragmentAKuGouViewModel::class.java) }

    private val viewModelDiscoverFragment by lazyMy { ViewModelProvider(this).get(DiscoverViewModel::class.java) }

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //??????BottomNavigationView?????????
      //  this.supportActionBar?.hide()
       initFragmentData()
       initView()
/*      bb = BottomSheetBehaviorMainActivity(InsideMainActivity(), binding.playerContainer, binding.layoutNavView.musicButton,
          R.id.bottom_sheet_behavior1,R.id.playerContainer)

       lifecycle.addObserver(bb)*/

     musicButton = binding.layoutNavView.musicButton
      musicButton.isDisplayText(false)//??????????????????

       // com.example.yuehaoting.theme.StatusBarUtil.setColorNoTranslucent(this, Color.RED)
     //   StatusBarUtil.setTransparent(this)
      //  StatusBarUtil.setColorForDrawerLayout(this, binding.drawer, Color.RED)
      //  StatusBarUtil.setColorForDrawerLayout(this, binding.drawer, Color.RED,StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
        ImmersionBar.with(this).statusBarDarkFont(true) .init()
    }

    class InsideMainActivity: InsideMainActivityBase {
        override val activity: BaseActivity
            get() = MainActivity()
    }

    /**
     * ?????????fragment??????
     */
    private fun initFragmentData() {
        viewModelMainFragment1.kuGouSpecialRecommendViewModel(pots())


        viewModelFragment1All.kuGouSpecialRecommendViewModel(1, 5)
    }

    override fun onPause() {
        super.onPause()
        isThread(true)
    }

    @SuppressLint("WrongConstant")
    private fun initView() {

        //???????????????
        val layout = DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT)
        layout.gravity = Gravity.START
        binding.llMainLeft.layoutParams = layout
        //??????????????????
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

        //???????????????
        val navView: BottomNavigationView = binding.layoutNavView.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)
        navView.setOnNavigationItemSelectedListener {
            if (it.isChecked) {
                return@setOnNavigationItemSelectedListener true
            }
            // ??????B?????????A????????????
            val popBackStack = navController.popBackStack(it.itemId, false)
            Log.e("DiscoverFragment", popBackStack.toString())
            if (popBackStack) {
                // ?????????
                return@setOnNavigationItemSelectedListener popBackStack
            } else {
                // ?????????
                return@setOnNavigationItemSelectedListener NavigationUI.onNavDestinationSelected(
                    it, navController
                )
            }
        }

    }


   override fun onServiceConnected(service: MusicService) {
        super.onServiceConnected(service)
        onPlayStateChange()
    }


    private var currentTime = 0

    private var duration = 0

    override fun onMetaChanged() {
        super.onMetaChanged()
        //???????????????
        val temp = MusicServiceRemote.getProgress()
        currentTime = if (temp in 1 until duration) temp else 0
        duration = MusicServiceRemote.getDuration()
        musicButton.setTotalProgress(duration)
        isThread(false)
    }

    //?????????????????????
   override fun onPlayStateChange() {
        super.onPlayStateChange()

        //??????????????????
        val isPlayful = MusicServiceRemote.isPlaying()
        if (isPlayful) {
            musicButton.playMusic(2)
            musicButton.playMusic(1)
           isThread(false)
        } else {
            musicButton.playMusic(3)
           isThread(true)

        }
    }

    private var isThreadStarted = false

    private inner class ProgressThread : Thread() {
        override fun run() {
            while (false) {
                try {
                    Log.e("????????????1", name)
                    if (isThreadStarted) {
                        break
                    }
                    val progress = MusicServiceRemote.getProgress()
                    if (progress in 1 until duration) {
                        runOnUiThread {
                      //      musicButton.setProgress(progress)
                        }
                        Log.e(getSecond(progress).toString(), getSecond(duration).toString())  //?????????????????????????????????
                        if (getSecond(progress) == getSecond(duration)) {
                            runOnUiThread {
                         //       musicButton.playMusic(4)
                            }
                        }
                        sleep(500)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

     fun getIsThread():Boolean {
      return this.isThreadStarted
    }

    private fun isThread(isThread: Boolean) {
        this.isThreadStarted = isThread
    }

    override fun onResume() {
        super.onResume()
      //  isThread(false)
       // ProgressThread().start()
    }

    /**
     * ??????fragment????????????????????????
     */
    @SuppressLint("RtlHardcoded")
    override fun activityMonitoringFragment() {
        Timber.v("?????????", "????????????")
        Timber.v("openDrawer%s", Gravity.RIGHT)
        binding.drawer.openDrawer(Gravity.RIGHT)
    }


    //????????????????????????????????????
    private val minIMumSpeed = 1000

    //????????????????????????????????????
    private val shortestDistance = 50

    //??????????????????????????????????????????
    private val minimumDistanceToSlide = 100

    //????????????????????????????????????
    private var xDown = 0f

    //????????????????????????????????????
    private var yDown = 0f

    //????????????????????????????????????
    private var xMove = 0f

    //????????????????????????????????????
    private var yMove = 0f

    //????????????????????????????????????
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
                //???????????????
                distanceX = (xMove - xDown).toInt()
                distanceY = (yMove - yDown).toInt()
                //??????????????????
                ySpeed = getScrollVelocity()
            }
            MotionEvent.ACTION_UP -> {
                recycleVelocityTracker()
                //??????Activity????????????????????????
                //1.x??????????????????>XDISTANCE_MIN
                //2.y?????????????????????YDISTANCE_MIN?????????
                //3.y????????????????????????????????????<XSPEED_MIN???????????????????????????????????????????????????????????????????????????Activity
                val fragment = intent.getIntExtra("fragment", 1)
                val discover = intent.getIntExtra("discover", 1)
                if (fragment == 0 && isDrawer && discover == 0) {
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
     * ??????VelocityTracker???????????????????????????????????????????????????VelocityTracker?????????
     *
     * @param event ??????????????????
     */
    private fun createVelocityTracker(event: MotionEvent) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
    }

    /**
     * ??????VelocityTracker?????????
     */
    private fun recycleVelocityTracker() {
        mVelocityTracker!!.recycle()
        mVelocityTracker = null
    }

    /**
     *
     * @return ???????????????????????????????????????????????????????????????
     */
    private fun getScrollVelocity(): Int {
        mVelocityTracker!!.computeCurrentVelocity(1000)
        val velocity = mVelocityTracker!!.yVelocity.toInt()
        return abs(velocity)
    }

    override fun onDestroy() {
        super.onDestroy()
        isThread(true)
    }


    /**
     * ?????????????????? ?????????
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



