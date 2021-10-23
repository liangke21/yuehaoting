package com.example.yuehaoting.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.App
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.BaseActivity
import com.example.yuehaoting.base.db.DatabaseRepository
import com.example.yuehaoting.base.diskLruCache.myCache.CacheString
import com.example.yuehaoting.base.diskLruCache.myCache.CacheUrl
import com.example.yuehaoting.base.log.LogT
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.base.view.view.MusicButtonLayout
import com.example.yuehaoting.callback.MusicEvenCallback
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.ActivityMainLayoutBottomSheetBehaviorBinding
import com.example.yuehaoting.kotlin.*
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.playInterface.activity.*
import com.example.yuehaoting.playInterface.framelayou.PlayPauseView
import com.example.yuehaoting.playInterface.viewmodel.PlayViewModel
import com.example.yuehaoting.theme.StatusBarUtil
import com.example.yuehaoting.theme.Theme
import com.example.yuehaoting.theme.ThemeStore
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MyUtil
import com.example.yuehaoting.util.Tag
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tencent.bugly.Bugly.applicationContext
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

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
class BottomSheetBehaviorMainActivity
    (
    private var _activity: InsideMainActivityBase? = null,
    private var _binding: ActivityMainLayoutBottomSheetBehaviorBinding? = null,
    private var musicButton: MusicButtonLayout,
    private val behavior2: LinearLayout
) : LifecycleObserver, MusicEvenCallback, ActivityHandlerCallback, View.OnClickListener, CoroutineScope by MainScope() {

    private val activity get() = _activity!!.activity
    private var baseActivity: BaseActivity = _activity!!.activity
    private val binding get() = _binding!!

    //_______________________________________||______________________________________________________________________________________________________
    private val viewModel by lazyMy { ViewModelProvider(activity).get(PlayViewModel::class.java) }
    private val mCacheUrl = CacheUrl()
    private val mCacheString = CacheString()
    private lateinit var playActivityColor: MainPlayActivityColor

    private lateinit var ppvPlayPause: PlayPauseView

    /**
     * 当前是否播放
     */
    private var isPlaying = false

    /**
     * 当前播放歌曲
     */
    private lateinit var currentSong: SongLists

    /**
     * 背景
     */
    private val background by lazyMy {
        getSp(activity, MusicConstant.NAME) {
            getInt(MusicConstant.PLAYER_BACKGROUND, MusicConstant.BACKGROUND_CUSTOM_IMAGE)
        }
    }

    /**
     * 第一次更新封面
     */
    private var isUpdateReceiveIntent by Delegates.notNull<Boolean>()

    /**
     * 写真背景
     */
    private var isPhotoBackground = true


    private var handler: PlayActivityHandler by lazyMy {
        PlayActivityHandler(this)
    }


    init {
        initView()
    }

    /**
     * BottomSheetBehavior初始化
     */
    private fun initView() {
        val behavior1 = BottomSheetBehavior.from(behavior2)
        val behavior2 = BottomSheetBehavior.from(binding.playerContainer)

        //展开
        behavior1.state = BottomSheetBehavior.STATE_EXPANDED

        behavior2.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.e("onStateChanged", newState.toString())

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior1.state = BottomSheetBehavior.STATE_EXPANDED
                    dropDownDestroy()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        musicButton.setOnClickListener {
            baseActivity.addMusicServiceEventListener(this)
            behavior2.state = BottomSheetBehavior.STATE_EXPANDED

            behavior1.state = BottomSheetBehavior.STATE_COLLAPSED

            onCreate()
            pullUpCreate()

        }
        MainSingerPhoto.setPlayPhoto(true)
        launch {
            withContext(Dispatchers.IO){
                MainSingerPhoto.playCycleCoroutine( binding.background, activity.resources, ::updateUi)
            }
        }
     //   MainSingerPhoto.playCycleThread( binding.background, activity.resources, ::updateUi)
    }

    /**
     * 上拉初始化
     */
    private fun pullUpCreate() {

        isProgressThread = true
        MainSingerPhoto.setPhoto(true)
        launch(Dispatchers.IO) {
            progressThread()

        }

    }


    override fun onMediaStoreChanged() {

    }

    override fun onPermissionChanged(has: Boolean) {

    }

    override fun onPlayListChanged(name: String) {

    }

    override fun onServiceDisConnected() {

    }

    override fun onTagChanged(oldSong: SongLists, newSongLists: SongLists) {

    }


    fun setSatuBarColor() {
        when (background) {
            // 背景自适应  更是封面
            MusicConstant.BACKGROUND_ADAPTIVE_COLOR -> {
                StatusBarUtil.setTransparent(activity)
                Timber.v("播放界面状态栏背景 背景自适应  更是封面 : %s", background)
            }
            //背景图片定义
            MusicConstant.BACKGROUND_CUSTOM_IMAGE -> {
                StatusBarUtil.setTransparent(activity)

                Glide.with(activity).asBitmap()
                    .load(R.drawable.youjing)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            binding.background.background = BitmapDrawable(activity.resources, resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    }
                    )
            }
        }
    }


    private fun onCreate() {

        getData()


        //初始化字符集合缓存
        mCacheUrl.init(activity)
        //字符集合
        mCacheString.init(activity, "Cover")
        //初始化ActivityColor
        playActivityColor = MainPlayActivityColor(binding, activity)

        receiveIntent(currentSong)
        isUpdateReceiveIntent = false

        observeSingerPhotoData()

        playActivityColor.setThemeColor()

        initView2()
        updateTopStatus(currentSong)

        seekBarRenew()

        onPlayStateChange()
    }

    private val repository = DatabaseRepository.getInstance()
    private fun getData() {
        currentSong = MusicServiceRemote.getCurrentSong()

        if (currentSong.id == -1L) {
            launch(Dispatchers.IO) {
                val quitId = getSp(applicationContext, MusicConstant.NAME) {
                    getLong(MusicConstant.QUIT_SONG_ID, -1L)
                }
                val queue = repository.getPlayQueueSongs().blockingGet()
                if (queue.isNotEmpty()) {
                    for (i in queue.indices) {
                        if (quitId == queue[i].id) {
                            currentSong = queue[i]
                        }
                    }
                }

            }
        }
    }

    /**
     * 当前歌曲时长
     */
    private var duration = 0

    /**
     * 当前播放时间
     */
    private var currentTime = 0

    /**
     * 是否正在拖动进度条
     */
    var isDragSeekBarFromUser = false

    /**
     * 歌曲进度条初始化
     */
    @SuppressLint("CheckResult")
    private fun seekBarRenew() {

        //初始化已播放时间与剩余时间
        duration = MusicServiceRemote.getDuration()
        val temp = MusicServiceRemote.getProgress()
        /*
        if (temp in 1 until duration) temp else 0
        in until 判断当前进度值 有没有在在当前时长里面
         */
        currentTime = if (temp in 1 until duration) temp else 0
        if (duration > 0 && duration - currentTime > 0) {
            binding.tvPlaySongStartingTime.text = MyUtil.getTime(currentTime.toLong())
            binding.tvPlaySongEndTime.text = MyUtil.getTime((duration - currentTime).toLong())
        }

        //初始化seekbar
        if (duration > 0 && duration < Int.MAX_VALUE) {
            binding.seekbar.max = duration
        } else {
            binding.seekbar.max = 1000
        }
        if (currentTime in 1 until duration) {
            binding.seekbar.progress = currentTime
        } else {
            binding.seekbar.progress = 0
        }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("LongLogTag")
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                //      Log.e("setOnSeekBarChangeListeneronProgressChanged", "$seekBar $progress $fromUser")
                if (fromUser) {
                    updateProgressText(progress)
                }
                handler.sendEmptyMessage(MusicConstant.UPDATE_TIME_ONLY)
                currentTime = progress
                // lrcView?.seekTo(progress, true, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isDragSeekBarFromUser = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //没有播放拖动进度条无效
//                if(!mIsPlay){
//                    seekBar.setProgress(0);
//                }
                MusicServiceRemote.setProgress(seekBar.progress)
                isDragSeekBarFromUser = false
            }
        })
    }

    override fun onUpdateProgressByHandler() {
        updateProgressByHandler()
    }

    override fun onUpdateSeekBarByHandler() {
        updateProgressByHandler()
        updateSeekBarByHandler()
    }

    /**
     * 更新进度条左右显示时间
     * @param progress Int
     */
    private fun updateProgressText(progress: Int) {

        if (progress > 0 && duration - progress > 0) {
            binding.tvPlaySongStartingTime.text = MyUtil.getTime(progress.toLong())
            binding.tvPlaySongEndTime.text = MyUtil.getTime((duration).toLong())
        }
    }

    /**
     * Handler 更新 进度条两端显示时间
     */
    private fun updateProgressByHandler() {
        updateProgressText(currentTime)
    }

    /**
     * Handler 更新seekBar显示进度
     */
    private fun updateSeekBarByHandler() {
        binding.seekbar.progress = currentTime

        binding.ManyLyricsView.play(currentTime)
    }

    //是否更新进度条
    private var isProgressThread = false

    /**
     * 更新进度条线程
     */
    private suspend fun progressThread() {
        Log.d("progressThread", isProgressThread.toString())
        while (isProgressThread) {

            try {
                val progress = MusicServiceRemote.getProgress()
                if (progress in 1 until duration) {
                    currentTime = progress
                    handler.sendEmptyMessage(MusicConstant.UPDATE_TIME_ALL)
                    delay(500)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //初始化控件
    private fun initView2() {
        binding.ivPlayGuide01.visibility = View.GONE

        ppvPlayPause = activity.findViewById(R.id.ppv_play_pause)

        arrayOf(
            binding.layoutPlayLayout.ibPlayPlayMode,
            binding.layoutPlayLayout.ibPlayPreviousSong,
            binding.layoutPlayLayout.flPlayContainer,
            binding.layoutPlayLayout.ibPlayNextTrack,
            binding.layoutPlayLayout.ibMusicList
        ).forEach {
            it.setOnClickListener(onCtrlClick)
        }

        binding.llPlayIndicator.setOnClickListener(this)

        var isLyricsExpansion = true

        binding.ManyLyricsView.setOnClickListenerL {

            if (isLyricsExpansion) {
                binding.ivPlayGuide01.visibility = View.GONE
                val layout = binding.ManyLyricsView.layoutParams
                layout.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.ManyLyricsView.layoutParams = layout
                isLyricsExpansion = false
                Timber.v("展开歌词")

            } else {
                if (!backgroundMode) {
                    binding.ivPlayGuide01.visibility = View.VISIBLE
                }
                val layout = binding.ManyLyricsView.layoutParams
                layout.height = 320
                binding.ManyLyricsView.layoutParams = layout
                isLyricsExpansion = true
            }
        }

        //  initLyrics()
    }


    /**
     * 歌词解析器
     */
    private fun initLyrics() {

        launchMy {
            try {
                Log.e("initLyrics()", currentSong.toString())
                val mLyricsReader = PlatformLyrics.lyrics(currentSong)
                binding.ManyLyricsView.apply {
                    initLrcData()
                    lyricsReader = mLyricsReader
                    setPaintColor(intArrayOf(-1, -2))
                    setPaintHLColor(intArrayOf(Color.GREEN, Color.YELLOW), true)
                    setSize(55, 35)
                    setIndicatorFontSize(55)  //圆半径
                    setOnLrcClickListener {
                        MusicServiceRemote.setProgress(it)
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 更新顶部标题
     */
    private fun updateTopStatus(currentSong: SongLists) {
        //标题栏设置 歌手歌词
        binding.layoutPlayLayoutBar.apply {
            tvPlaySongName.text = currentSong.SongName
            Timber.v("currentSong:%s", currentSong)
            tvPlaySingerName.text = currentSong.SingerName
        }
    }
    private var singerId = ""
    /**
     * 接收数据
     * 2325 表示歌曲写真id来自 HifIni
     */
    private fun receiveIntent(currentSong: SongLists) {
        // val singerId = intent.getStringExtra(SINGER_ID)

        val singerId = currentSong.mixSongID
        if (this.singerId==singerId){
            return
        }
        if (singerId != "") {
            val list = mCacheUrl.getFromDisk(singerId)
            Timber.v("歌手写真url缓存文件:%s", list?.size)
            if (list != null) {
    /*            launch {
                    withContext(Dispatchers.IO) {
                        MainSingerPhoto.playCycle(list, binding.background, activity.resources, ::updateUi)
                    }

                }*/


                        MainSingerPhoto.setUrlList(list,singerId)





            } else {
                Timber.v("歌手id: %S", singerId)

                Glide.with(activity).asBitmap()
                    .load(R.drawable.youjing)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            binding.background.background = BitmapDrawable(activity.resources, resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })

                viewModel.singerId(singerId)
            }

        } else {
            Glide.with(activity).asBitmap()
                .load(R.drawable.youjing)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        binding.background.background = BitmapDrawable(activity.resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }


    /**
     * 观察写真图片
     */
    private fun observeSingerPhotoData() {


        tryNull {
            viewModel.singerIdObservedData.observe(activity) {
                //获取图片连接
                val urlList = MainSingerPhoto.singerPhotoUrl(it)
                val singerId = currentSong.mixSongID
                mCacheUrl.putToDisk(singerId, urlList)
                //把图片设置为背景
                //////////////////////    MainSingerPhoto.playCycle(urlList, binding.background, activity.resources, ::updateUi)
            }
        }

    }

    /**
     *   上一首 播放暂停 下一首
     */
    private val onCtrlClick = View.OnClickListener { v ->
        val intent = Intent(MusicConstant.ACTION_CMD)
        when (v.id) {
            binding.layoutPlayLayout.ibPlayPlayMode.id -> {
                var currentModel = MusicServiceRemote.getPlayModel()

                Timber.v("播放模式 %s", currentModel)


                currentModel = if (currentModel == MusicConstant.SINGLE_CYCLE) MusicConstant.LIST_LOOP else ++currentModel
                MusicServiceRemote.setPlayModel(currentModel)
                binding.layoutPlayLayout.ibPlayPlayMode.setImageDrawable(
                    Theme.tintDrawable(
                        when (currentModel) {
                            MusicConstant.LIST_LOOP -> R.drawable.play_btn_loop
                            MusicConstant.RANDOM_PATTERN -> R.drawable.play_btn_shuffle
                            else -> R.drawable.play_btn_loop_one
                        }, ThemeStore.playerBtnColor
                    )
                )

                val msg =
                    when (currentModel) {
                        MusicConstant.LIST_LOOP -> activity.getString(R.string.model_normal)
                        MusicConstant.RANDOM_PATTERN -> activity.getString(
                            R.string.model_random
                        )
                        else -> activity.getString(R.string.model_repeat)
                    }

                msg.showToast(activity)
            }
            binding.layoutPlayLayout.ibPlayPreviousSong.id -> {
                intent.putExtra(MusicConstant.EXTRA_CONTROL, MusicConstant.PREV)
                Timber.v("播放上一首1: %s", MusicConstant.PREV)
            }

            binding.layoutPlayLayout.flPlayContainer.id -> intent.putExtra(
                MusicConstant.EXTRA_CONTROL,
                MusicConstant.PAUSE_PLAYBACK
            )

            binding.layoutPlayLayout.ibPlayNextTrack.id -> {
                intent.putExtra(MusicConstant.EXTRA_CONTROL, MusicConstant.NEXT)
                Timber.v("播放下一首1: %s", MusicConstant.NEXT)
            }
            binding.layoutPlayLayout.ibMusicList.id -> {
                PlayActivityDialogFragment.newInstance()
                    .show(activity.supportFragmentManager, PlayActivityDialogFragment::class.java.simpleName)
            }
        }

        BroadcastUtil.sendLocalBroadcast(intent)
    }

    private val observableCurrentSong = ObservableCurrentSong()
    private var recordingCurrentSong: String? = ""

    /**
     * 元数据更改
     */
    override fun onMetaChanged() {

        currentSong = MusicServiceRemote.getCurrentSong()
        //更新标题
        updateTopStatus(currentSong)
        //更新进度条
        val temp = MusicServiceRemote.getProgress()
        currentTime = if (temp in 1 until duration) temp else 0
        duration = MusicServiceRemote.getDuration()
        binding.seekbar.max = duration

        //   initLyrics()

        //播放界面写真和封面更改
        observableCurrentSong.nameCurrentSong = currentSong.mixSongID
        if (recordingCurrentSong != currentSong.mixSongID && isUpdateReceiveIntent) {
            //更新封面
            if (isPhotoBackground) {
                receiveIntent(currentSong)
            } else {
                showCover()
            }

            recordingCurrentSong = currentSong.mixSongID
        }
        isUpdateReceiveIntent = true

        Log.e(" isUpdateReceiveIntent", isUpdateReceiveIntent.toString())
    }

    override fun onServiceConnected(service: MusicService) {

        onPlayStateChange()
    }

    //播放状态已更改
    override fun onPlayStateChange() {

        //更新按钮状态
        val isPlayful = MusicServiceRemote.isPlaying()
        Timber.tag(Tag.isPlay).v("前台播放图标转态:%s,后台传入状态:%s,:%s", isPlaying, isPlayful, LogT.lll())
        if (isPlaying != isPlayful) {
            updatePlayButton(isPlayful)
        }

    }

    /**
     * 更新播放暂停按钮
     */
    private fun updatePlayButton(isPlayful: Boolean) {
        isPlaying = isPlayful
        Timber.tag(Tag.isPlay).v("前台播放图标更新:%s,后台传入状态:%s,:%s", isPlayful, isPlaying, LogT.lll())
        ppvPlayPause.updateStRte(isPlayful, true)


        // revisePlaying()
        Timber.tag(Tag.isPlay).v(
            "======================================================================:%s",
            currentSong.SongName
        )
    }


    /**
     * Bitmap 里面获取颜色
     */
    @SuppressLint("CheckResult")
    fun updateUi(bitmap: Bitmap, boolean: Boolean) {

        Single.fromCallable { bitmap }.map { result ->
            val palette = Palette.from(result).generate()
            if (palette.mutedSwatch != null) {
                return@map palette.mutedSwatch
            }
            val swatches = ArrayList<Palette.Swatch>(palette.swatches)

            swatches.sortWith { O1, O2 -> O1.population.compareTo(O2.population) }
            return@map if (swatches.isEmpty()) swatches[0] else Palette.Swatch(Color.GRAY, 100)

        }
            .onErrorReturnItem((Palette.Swatch(Color.GRAY, 100)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ swatch ->

                if (swatch == null) {
                    return@subscribe
                }
                playActivityColor.updateViewsColor(swatch, boolean)
            }) { t: Throwable? -> Timber.v(t) }


    }

    /**
     * 背景模式
     */
    private var backgroundMode = true
    override fun onClick(v: View?) {
        when (v?.id) {
            binding.llPlayIndicator.id -> {
                if (backgroundMode) {
                    isPhotoBackground = false
                    Timber.e("===============================================================================")
                    //结束写真幻影灯片
                    SingerPhoto.handlerRemoveCallbacks()
                    showCover()
                    backgroundMode = false
                } else {
                    isPhotoBackground = true
                    binding.ivPlayGuide01.visibility = View.GONE
                    backgroundMode = true
                    receiveIntent(currentSong)

                }
            }

        }
    }


    /**
     * 显示封面
     */
    @SuppressLint("CheckResult")
    private fun showCover() {
        //图片圆角
        val requestOptions = RequestOptions()
        // requestOptions.placeholder(R.drawable.ic_launcher_background)
        RequestOptions.circleCropTransform()
        requestOptions.transform(RoundedCorners(30))

        launchMain {

            val uriID = SongNetwork.songUriID(currentSong.FileHash, "")
            val key = currentSong.FileHash.lowercase(Locale.ROOT)
            val img = mCacheString.getFromDisk(key)
            if (img != null) {
                Timber.v("加载本地封面:%s", img)
                Glide.with(App.context).asBitmap()
                    .apply(requestOptions)
                    .load(img)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            binding.ivPlayGuide01.visibility = View.VISIBLE
                            binding.ivPlayGuide01.setImageBitmap(resource)
                            updateUi(resource, true)


                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            } else {
                val pic = uriID.data.img
                mCacheString.putToDisk(key, pic)

                Timber.v("加载网络封面:%s", img)
                Glide.with(App.context).asBitmap()
                    .apply(requestOptions)
                    .placeholder(R.drawable.play_activity_album)
                    .load(pic)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            binding.ivPlayGuide01.setImageBitmap(resource)
                            //结束写真幻影灯片
                            SingerPhoto.handlerRemoveCallbacks()
                            updateUi(resource, true)


                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
                binding.ivPlayGuide01.visibility = View.VISIBLE
            }
        }


    }

    /**
     * 下拉关闭
     */
    private fun dropDownDestroy() {
        baseActivity.removeMusicServiceEventListener(this)
        isProgressThread = false
        mCacheUrl.close()
        mCacheString.close()
        repository.closure()
        MainSingerPhoto.setPhoto(false)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        cancel()
        _activity = null
        _binding = null
        baseActivity.removeMusicServiceEventListener(this)
        isProgressThread = false
        mCacheUrl.close()
        mCacheString.close()
        repository.closure()
        MainSingerPhoto.setPlayPhoto(false)
    }

}