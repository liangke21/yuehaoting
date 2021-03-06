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
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhotoData
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
import com.example.yuehaoting.util.MusicConstant.HIF_INI
import com.example.yuehaoting.util.MusicConstant.KU_GOU
import com.example.yuehaoting.util.MusicConstant.KU_WO
import com.example.yuehaoting.util.MusicConstant.MI_GU
import com.example.yuehaoting.util.MusicConstant.MUSIC_136
import com.example.yuehaoting.util.MusicConstant.NEW_SONG_KU_GOU
import com.example.yuehaoting.util.MusicConstant.QQ
import com.example.yuehaoting.util.MyUtil
import com.example.yuehaoting.util.Tag
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * ??????: LiangKe
 * ??????: 2021/10/9 9:51
 * @property activity MainActivity
 * @property binding ActivityMainBinding
 * @property musicButton MusicButtonLayout
 * @constructor
 *
 * BottomNavigationView ???????????????
 * BottomSheetBehavior ???????????????
 */
class BottomSheetBehaviorMainActivity
    (
    private var _activity: InsideMainActivityBase? = null,
    private var _binding: ActivityMainLayoutBottomSheetBehaviorBinding? = null,
    private var musicButton: MusicButtonLayout,
    private val behavior1: LinearLayout
) : LifecycleObserver, MusicEvenCallback, ActivityHandlerCallback, View.OnClickListener, CoroutineScope by MainScope() {

    private val activity get() = _activity!!.activity
    private var baseActivity: BaseActivity = _activity!!.activity
    private val binding get() = _binding!!

    //_______________________________________||______________________________________________________________________________________________________
    private val viewModel by lazy { ViewModelProvider(activity).get(PlayViewModel::class.java) }
    private val mCacheUrl = CacheUrl()
    private val mCacheString = CacheString()
    private lateinit var playActivityColor: MainPlayActivityColor

    private lateinit var ppvPlayPause: PlayPauseView

    lateinit var behavior2: BottomSheetBehavior<CoordinatorLayout>
    /**
     * ??????????????????
     */
    private var isPlaying = false

    /**
     * ??????????????????
     */
    private lateinit var currentSong: SongLists

    /**
     * ??????
     */
    private val background by lazyMy {
        getSp(activity, MusicConstant.NAME) {
            getInt(MusicConstant.PLAYER_BACKGROUND, MusicConstant.BACKGROUND_CUSTOM_IMAGE)
        }
    }

    /**
     * ?????????????????????
     */
    @Deprecated("??????,???????????????????????????")
    private var isUpdateReceiveIntent by Delegates.notNull<Boolean>()

    /**
     * ????????????
     */
    private var isPhotoBackground = true


    private var handler: PlayActivityHandler by lazyMy {
        PlayActivityHandler(this)
    }


    init {
        initView()
    }

    /**
     * BottomSheetBehavior?????????
     */
    private fun initView() {
        val behavior1 = BottomSheetBehavior.from(behavior1)
         behavior2 = BottomSheetBehavior.from(binding.playerContainer)

        //??????
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
            withContext(Dispatchers.IO) {
                MainSingerPhoto.playCycleCoroutine(binding.background, activity.resources, ::updateUi)
            }
        }
        //   MainSingerPhoto.playCycleThread( binding.background, activity.resources, ::updateUi)
    }

    /**
     * ???????????????
     */
    private fun pullUpCreate() {

        isProgressThread = true
        when (currentSong.platform) {

            KU_GOU, NEW_SONG_KU_GOU -> {
                MainSingerPhoto.setPhoto(true)
            }
            HIF_INI, MUSIC_136, QQ, KU_WO, MI_GU->{
                MainSingerPhoto.setPhoto(false)
            }
        }

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
            // ???????????????  ????????????
            MusicConstant.BACKGROUND_ADAPTIVE_COLOR -> {
                StatusBarUtil.setTransparent(activity)
                Timber.v("??????????????????????????? ???????????????  ???????????? : %s", background)
            }
            //??????????????????
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

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun create() {
        getData()
        observeSingerPhotoData()
        binding.ivPlayGuide01.visibility = View.GONE
    }

    private fun onCreate() {
        getData()
        //???????????????????????????
        mCacheUrl.init(activity)
        //????????????
        mCacheString.init(activity, "Cover")
        //?????????ActivityColor
        playActivityColor = MainPlayActivityColor(binding, activity)
        Timber.e("currentSong.platform onCreate() %s", currentSong.platform)


        receiveIntent(currentSong)



        isUpdateReceiveIntent = false



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
                val quitId = getSp(activity.applicationContext, MusicConstant.NAME) {
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
        } else {
            currentSong = MusicServiceRemote.getCurrentSong()
        }
    }

    /**
     * ??????????????????
     */
    private var duration = 0

    /**
     * ??????????????????
     */
    private var currentTime = 0

    /**
     * ???????????????????????????
     */
    var isDragSeekBarFromUser = false

    /**
     * ????????????????????????
     */
    @SuppressLint("CheckResult")
    private fun seekBarRenew() {

        //???????????????????????????????????????
        duration = MusicServiceRemote.getDuration()
        val temp = MusicServiceRemote.getProgress()
        /*
        if (temp in 1 until duration) temp else 0
        in until ????????????????????? ?????????????????????????????????
         */
        currentTime = if (temp in 1 until duration) temp else 0
        if (duration > 0 && duration - currentTime > 0) {
            binding.tvPlaySongStartingTime.text = MyUtil.getTime(currentTime.toLong())
            binding.tvPlaySongEndTime.text = MyUtil.getTime((duration - currentTime).toLong())
        }

        //?????????seekbar
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
                //?????????????????????????????????
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
     * ?????????????????????????????????
     * @param progress Int
     */
    private fun updateProgressText(progress: Int) {

        if (progress > 0 && duration - progress > 0) {
            binding.tvPlaySongStartingTime.text = MyUtil.getTime(progress.toLong())
            binding.tvPlaySongEndTime.text = MyUtil.getTime((duration).toLong())
        }
    }

    /**
     * Handler ?????? ???????????????????????????
     */
    private fun updateProgressByHandler() {
        updateProgressText(currentTime)
    }

    /**
     * Handler ??????seekBar????????????
     */
    private fun updateSeekBarByHandler() {
        binding.seekbar.progress = currentTime

        binding.ManyLyricsView.play(currentTime)
    }

    //?????????????????????
    private var isProgressThread = false

    /**
     * ?????????????????????
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


    //???????????????
    private fun initView2() {
     binding.layoutPlayLayoutBar.ibPlayDropDown.setOnClickListener(this)

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
                Timber.v("????????????")

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

       initLyrics()
    }


    /**
     * ???????????????
     */
    private fun initLyrics() {
        when (currentSong.platform) {

            HIF_INI, MUSIC_136, QQ, KU_WO, MI_GU->{
                binding.ManyLyricsView.apply {
                    binding.ManyLyricsView.visibility = View.GONE
                    return
                }
            }
        }

        launchMy {
            try {
                Timber.tag("??????").v("?????? %s", currentSong.platform)
                val mLyricsReader = PlatformLyrics.lyrics(currentSong) ?: return@launchMy
                binding.ManyLyricsView.apply {
                    initLrcData()
                    lyricsReader = mLyricsReader
                    setPaintColor(intArrayOf(-1, -2))
                    setPaintHLColor(intArrayOf(Color.GREEN, Color.YELLOW), true)
                    setSize(55, 35)
                    setIndicatorFontSize(55)  //?????????
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
     * ??????????????????
     */
    private fun updateTopStatus(currentSong: SongLists) {
        //??????????????? ????????????
        binding.layoutPlayLayoutBar.apply {
            tvPlaySongName.text = currentSong.SongName
            Timber.v("currentSong:%s", currentSong)
            tvPlaySingerName.text = currentSong.SingerName
        }
    }

    private var singerId = ""

    /**
     * ????????????
     * 2325 ??????????????????id?????? HifIni
     */
    private fun receiveIntent(currentSong: SongLists) {
        val singerId = currentSong.mixSongID

        when (currentSong.platform) {
            KU_GOU, NEW_SONG_KU_GOU -> {
                if (this.singerId == currentSong.SingerName) {
                    return
                }
            }
            HIF_INI, MUSIC_136, QQ, KU_WO, MI_GU->{
                defaultPhoto()
                showCover()
                backgroundMode=false
                binding.background.visibility = View.GONE
                return
            }

        }
        Timber.tag(Tag.singerPhoto).v("??????id %s  ??????id %s", singerId, currentSong.SingerName)

        if (singerId != "") {
            defaultPhoto()
            val list = mCacheUrl.getFromDisk(singerId)
            if (list != null) {
                Timber.tag(Tag.singerPhoto).v("????????????url?????????????????????:%s", list.size)
                MainSingerPhoto.setUrlList(list, singerId)
                list.clear()
            } else {

                Timber.tag(Tag.singerPhoto).v("????????????????????????: %S", singerId)

                defaultPhoto()
                viewModel.singerId(singerId)
            }

        } else {
            defaultPhoto()
        }
        this.singerId = currentSong.SingerName
    }

    /**
     * ,????????????
     */
    private fun defaultPhoto() {
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

    /**
     * ??????????????????
     */
    private fun observeSingerPhotoData() {


        viewModel.singerIdObservedData.observe(activity) {

            if (it.getOrNull() == null) {
                return@observe
            }
            val data = it.getOrNull() as SingerPhotoData
            val phoneSingerPhoto = data.data[0][0].imgs.`4` ?:return@observe

            Timber.tag(Tag.singerPhoto).v("?????????????????????????????????: %S ?????? %s", singerId, data.data[0][0].author_name)
            //??????????????????
            val urlList = MainSingerPhoto.singerPhotoUrl(phoneSingerPhoto)
            val singerId = currentSong.mixSongID
            mCacheUrl.putToDisk(singerId, urlList)
            //????????????????????????
            //////////////////////    MainSingerPhoto.playCycle(urlList, binding.background, activity.resources, ::updateUi)
            MainSingerPhoto.setUrlList(urlList, singerId)
            urlList.clear()
        }


    }

    /**
     *   ????????? ???????????? ?????????
     */
    private val onCtrlClick = View.OnClickListener { v ->
        val intent = Intent(MusicConstant.ACTION_CMD)
        when (v.id) {
            binding.layoutPlayLayout.ibPlayPlayMode.id -> {
                var currentModel = MusicServiceRemote.getPlayModel()

                Timber.v("???????????? %s", currentModel)


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
                Timber.v("???????????????1: %s", MusicConstant.PREV)
            }

            binding.layoutPlayLayout.flPlayContainer.id -> intent.putExtra(
                MusicConstant.EXTRA_CONTROL,
                MusicConstant.PAUSE_PLAYBACK
            )

            binding.layoutPlayLayout.ibPlayNextTrack.id -> {
                intent.putExtra(MusicConstant.EXTRA_CONTROL, MusicConstant.NEXT)
                Timber.v("???????????????1: %s", MusicConstant.NEXT)
            }
            binding.layoutPlayLayout.ibMusicList.id -> {
                PlayActivityDialogFragment.newInstance()
                    .show(activity.supportFragmentManager, PlayActivityDialogFragment::class.java.simpleName)
            }
        }

        BroadcastUtil.sendLocalBroadcast(intent)
    }

    @Deprecated("??????")
    private val observableCurrentSong = ObservableCurrentSong()
    private var recordingCurrentSong: String? = ""

    /**
     * ???????????????
     */
    override fun onMetaChanged() {

        currentSong = MusicServiceRemote.getCurrentSong()
        //????????????
        updateTopStatus(currentSong)
        //???????????????
        val temp = MusicServiceRemote.getProgress()
        currentTime = if (temp in 1 until duration) temp else 0
        duration = MusicServiceRemote.getDuration()
        binding.seekbar.max = duration

        initLyrics()

/*        //?????????????????????????????????
        observableCurrentSong.nameCurrentSong = currentSong.mixSongID
        if (recordingCurrentSong != currentSong.mixSongID *//*&& isUpdateReceiveIntent*//*) {
            //????????????
            if (isPhotoBackground) {
                Log.e("??????111111??????","")
                receiveIntent(currentSong)
            } else {
                Log.e("??????111111??????","")
                showCover()
            }

            recordingCurrentSong = currentSong.mixSongID
        }
        isUpdateReceiveIntent = true*/

        Log.e(" isUpdateReceiveIntent", isUpdateReceiveIntent.toString())

        when (currentSong.platform) {
            HIF_INI, MUSIC_136, QQ, KU_WO, MI_GU -> {
                //????????????
                Timber.e("currentSong.platform %s", currentSong.platform)
                MainSingerPhoto.setPhoto(false)
                showCover()

            }
            KU_GOU, NEW_SONG_KU_GOU -> {
                //????????????
                if (isPhotoBackground) {
                    Log.e("??????111111??????", "")
                    receiveIntent(currentSong)
                } else {
                    Log.e("??????111111??????", "")
                    showCover()
                }

            }

        }

    }

    override fun onServiceConnected(service: MusicService) {

        onPlayStateChange()
    }

    //?????????????????????
    override fun onPlayStateChange() {

        //??????????????????
        val isPlayful = MusicServiceRemote.isPlaying()
        Timber.tag(Tag.isPlay).v("????????????????????????:%s,??????????????????:%s,:%s", isPlaying, isPlayful, LogT.lll())
        if (isPlaying != isPlayful) {
            updatePlayButton(isPlayful)
        }

    }

    /**
     * ????????????????????????
     */
    private fun updatePlayButton(isPlayful: Boolean) {
        isPlaying = isPlayful
        Timber.tag(Tag.isPlay).v("????????????????????????:%s,??????????????????:%s,:%s", isPlayful, isPlaying, LogT.lll())
        ppvPlayPause.updateStRte(isPlayful, true)


        // revisePlaying()
        Timber.tag(Tag.isPlay).v(
            "======================================================================:%s",
            currentSong.SongName
        )
    }


    /**
     * Bitmap ??????????????????
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
     * ????????????
     */
    private var backgroundMode = true
    override fun onClick(v: View?) {
        when (v?.id) {
            binding.llPlayIndicator.id -> {
                if (backgroundMode) {
                    isPhotoBackground = false
                    Timber.e("===============================================================================")
                    //????????????????????????
                    //   SingerPhoto.handlerRemoveCallbacks()
                    binding.background.visibility = View.GONE
                    showCover()
                    backgroundMode = false
                } else {
                    isPhotoBackground = true
                    binding.ivPlayGuide01.visibility = View.GONE
                    backgroundMode = true
                    binding.background.visibility = View.VISIBLE

                }
            }

         binding.layoutPlayLayoutBar.ibPlayDropDown.id->{
             behavior2.state=BottomSheetBehavior.STATE_COLLAPSED
         }

        }
    }


    /**
     * ????????????
     */
    @SuppressLint("CheckResult")
    private fun showCover() {
        when (currentSong.platform) {
            KU_GOU, NEW_SONG_KU_GOU -> {
                if (isPhotoBackground) {
                    return
                }
            }
        }
        //????????????
        val requestOptions = RequestOptions()
        // requestOptions.placeholder(R.drawable.ic_launcher_background)
        RequestOptions.circleCropTransform()
        requestOptions.transform(RoundedCorners(30))

        launchMain {

            if (currentSong.platform == -1) {
                return@launchMain
            }
            var pic = ""
            //???????????????????????????
            when (currentSong.platform) {
                KU_GOU -> {
                    val uriID = SongNetwork.songUriID(currentSong.FileHash, "")
                    pic = uriID.data.img
                }
                HIF_INI -> {
                    pic = getSp(activity, MusicConstant.NAME) {
                        getString(MusicConstant.HIF_INI_PIC, "")!!
                    }
                }
                MUSIC_136 -> {
                    pic = "https://myhkw.cn/api/musicPic?picId=${currentSong.pic}&type=wy&size=big"
                }
                QQ -> {
                    Timber.v("currentSong.pic %s", currentSong.pic)
                    pic = "https://myhkw.cn/api/musicPic?picId=${currentSong.pic}&type=qq&size=big"
                }

                KU_WO -> {
                    val str = SongNetwork.musicKuWoPic(currentSong.pic)
                    val sb = StringBuilder()
                    str.source().inputStream().use { inp ->
                        InputStreamReader(inp).use { isp ->
                            BufferedReader(isp).use { br ->
                                br.use {
                                    br.forEachLine {
                                        sb.append(it)
                                    }
                                }
                            }
                        }
                    }
                    val json = JSONObject(sb.toString())
                    pic = json.getString("url")
                    sb.delete(0, sb.length)
                }

                MI_GU -> {
                    pic = currentSong.pic
                }
            }

            val key = currentSong.FileHash.lowercase(Locale.ROOT)
            val img = mCacheString.getFromDisk(key)



            if (img != null) {
                Timber.v("??????????????????:%s", img)
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
                //  val pic = uriID.data.img
                mCacheString.putToDisk(key, pic)

                Timber.v("??????????????????:%s", img)
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
                            //????????????????????????
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
     * ????????????
     */
    private fun dropDownDestroy() {
        baseActivity.removeMusicServiceEventListener(this)
        //  isProgressThread = false
        mCacheUrl.close()
        mCacheString.close()
        MainSingerPhoto.setPhoto(false)
        viewModel.cleared()
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