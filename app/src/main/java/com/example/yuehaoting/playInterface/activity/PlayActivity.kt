package com.example.yuehaoting.playInterface.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.*
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.App
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.PlayBaseActivity
import com.example.yuehaoting.base.diskLruCache.myCache.CacheString
import com.example.yuehaoting.base.diskLruCache.myCache.CacheUrl
import com.example.yuehaoting.base.log.LogT
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhotoData
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.PlayActivityBinding
import com.example.yuehaoting.kotlin.*
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.musicService.service.MusicServiceRemote.getCurrentSong
import com.example.yuehaoting.musicService.service.MusicServiceRemote.getDuration
import com.example.yuehaoting.musicService.service.MusicServiceRemote.getPlayModel
import com.example.yuehaoting.musicService.service.MusicServiceRemote.getProgress
import com.example.yuehaoting.musicService.service.MusicServiceRemote.isPlaying
import com.example.yuehaoting.musicService.service.MusicServiceRemote.setPlayModel
import com.example.yuehaoting.playInterface.activity.PlayActivityDialogFragment.Companion.newInstance
import com.example.yuehaoting.playInterface.activity.SingerPhoto.handlerRemoveCallbacks
import com.example.yuehaoting.playInterface.activity.SingerPhoto.photoCycle
import com.example.yuehaoting.playInterface.activity.SingerPhoto.singerPhotoUrl
import com.example.yuehaoting.playInterface.framelayou.PlayPauseView
import com.example.yuehaoting.playInterface.viewmodel.PlayViewModel
import com.example.yuehaoting.theme.*
import com.example.yuehaoting.theme.StatusBarUtil
import com.example.yuehaoting.util.*
import com.example.yuehaoting.util.MusicConstant.ACTION_CMD
import com.example.yuehaoting.util.MusicConstant.BACKGROUND_ADAPTIVE_COLOR
import com.example.yuehaoting.util.MusicConstant.BACKGROUND_CUSTOM_IMAGE
import com.example.yuehaoting.util.MusicConstant.CURRENT_SONG
import com.example.yuehaoting.util.MusicConstant.EXTRA_CONTROL
import com.example.yuehaoting.util.MusicConstant.LIST_LOOP
import com.example.yuehaoting.util.MusicConstant.NAME
import com.example.yuehaoting.util.MusicConstant.NEXT
import com.example.yuehaoting.util.MusicConstant.PAUSE_PLAYBACK
import com.example.yuehaoting.util.MusicConstant.PLAYER_BACKGROUND
import com.example.yuehaoting.util.MusicConstant.PREV
import com.example.yuehaoting.util.MusicConstant.RANDOM_PATTERN
import com.example.yuehaoting.util.MusicConstant.SINGLE_CYCLE
import com.example.yuehaoting.util.MusicConstant.UPDATE_TIME_ALL
import com.example.yuehaoting.util.MusicConstant.UPDATE_TIME_ONLY
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class PlayActivity : PlayBaseActivity(), View.OnClickListener, ActivityHandlerCallback {
    private lateinit var binding: PlayActivityBinding
    // private val myUtil = BroadcastUtil()

    private val viewModel by lazyMy { ViewModelProvider(this).get(PlayViewModel::class.java) }
    private val mCacheUrl = CacheUrl()
    private val mCacheString = CacheString()
    private lateinit var playActivityColor: PlayActivityColor

    private lateinit var ppvPlayPause: PlayPauseView

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
        getSp(this, NAME) {
            getInt(PLAYER_BACKGROUND, BACKGROUND_CUSTOM_IMAGE)
        }
    }

    /**
     * ?????????????????????
     */
    private var isUpdateReceiveIntent by Delegates.notNull<Boolean>()

    /**
     * ????????????
     */
    private var isPhotoBackground = true

    private val handler: PlayActivityHandler by lazyMy {
        PlayActivityHandler( this)
    }

    override fun setSatuBarColor() {
        when (background) {
            // ???????????????  ????????????
            BACKGROUND_ADAPTIVE_COLOR -> {
                StatusBarUtil.setTransparent(this)
                Timber.v("??????????????????????????? ???????????????  ???????????? : %s", background)
            }
            //??????????????????
            BACKGROUND_CUSTOM_IMAGE -> {
                StatusBarUtil.setTransparent(this)

                Glide.with(this).asBitmap()
                    .load(R.drawable.youjing)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            binding.playerContainer.background = BitmapDrawable(resources, resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    }
                    )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlayActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentSong = getCurrentSong()

        if (currentSong == SongLists.SONG_LIST && intent.hasExtra(CURRENT_SONG)) {
            currentSong = intent.getParcelableExtra(CURRENT_SONG)!!
        }
        Timber.v("currentSong:%s %s ", intent.getParcelableExtra(CURRENT_SONG)!!, currentSong)
        //???????????????????????????
        mCacheUrl.init(this)
        //????????????
        mCacheString.init(this, "Cover")
        //?????????ActivityColor
        playActivityColor = PlayActivityColor(binding, this)

        receiveIntent(currentSong)
        isUpdateReceiveIntent = false

        observeSingerPhotoData()

        playActivityColor.setThemeColor()

        initView()
        updateTopStatus(currentSong)

        seekBarRenew()


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
        duration = getDuration()
        val temp = getProgress()
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

                Log.e("setOnSeekBarChangeListeneronProgressChanged", "$seekBar $progress $fromUser")
                if (fromUser) {
                    updateProgressText(progress)
                }
                handler.sendEmptyMessage(UPDATE_TIME_ONLY)
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

    /**
     * ?????????????????????
     */
    private inner class ProgressThread : Thread() {
        override fun run() {
            while (isForeground) {
                try {
                    val progress = getProgress()
                    if (progress in 1 until duration) {
                        currentTime = progress
                        handler.sendEmptyMessage(UPDATE_TIME_ALL)
                        sleep(500)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    //???????????????
    private fun initView() {
        binding.ivPlayGuide01.visibility = View.GONE

        ppvPlayPause = findViewById(R.id.ppv_play_pause)

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
               if (!backgroundMode){
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

    override fun onResume() {
        super.onResume()
        ProgressThread().start()
    }

    /**
     * ???????????????
     */
    private fun initLyrics() {

        launchMy {
            try {
                val mLyricsReader = PlatformLyrics.lyrics(currentSong)
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

    /**
     * ????????????
     * 2325 ??????????????????id?????? HifIni
     */
    private fun receiveIntent(currentSong: SongLists) {
        // val singerId = intent.getStringExtra(SINGER_ID)
        val singerId = currentSong.mixSongID
        if (singerId != "2325") {
            val list = mCacheUrl.getFromDisk(singerId)
            Timber.v("????????????url????????????:%s", list?.size)
            if (list != null) {
                photoCycle(list, binding.playerContainer, resources, ::updateUi)
            } else {
                Timber.v("??????id: %S", singerId)

                Glide.with(App.context).asBitmap()
                    .load(R.drawable.youjing)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            binding.playerContainer.background = BitmapDrawable(resources, resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })

              //////////////////////////////////////////////////////////////  viewModel.singerId(singerId)
            }

        } else {
            Glide.with(App.context).asBitmap()
                .load(R.drawable.youjing)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        binding.playerContainer.background = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    private fun observeSingerPhotoData() {


        tryNull {
            viewModel.singerIdObservedData.observe(this) {
                //??????????????????
                if (it.getOrNull()==null){
                    return@observe
                }
                val data= it.getOrNull() as SingerPhotoData
                val  phoneSingerPhoto =  data.data[0][0].imgs.`4`
                val urlList = singerPhotoUrl(phoneSingerPhoto)
                val singerId = currentSong.mixSongID
                mCacheUrl.putToDisk(singerId, urlList)
                //????????????????????????
                photoCycle(urlList, binding.playerContainer, resources, ::updateUi)
            }
        }

    }

    /**
     *   ????????? ???????????? ?????????
     */
    private val onCtrlClick = View.OnClickListener { v ->
        val intent = Intent(ACTION_CMD)
        when (v.id) {
            binding.layoutPlayLayout.ibPlayPlayMode.id -> {
                var currentModel = getPlayModel()

                Timber.v("???????????? %s", currentModel)


                currentModel = if (currentModel == SINGLE_CYCLE) LIST_LOOP else ++currentModel
                setPlayModel(currentModel)
                binding.layoutPlayLayout.ibPlayPlayMode.setImageDrawable(
                    Theme.tintDrawable(
                        when (currentModel) {
                            LIST_LOOP -> R.drawable.play_btn_loop
                            RANDOM_PATTERN -> R.drawable.play_btn_shuffle
                            else -> R.drawable.play_btn_loop_one
                        }, ThemeStore.playerBtnColor
                    )
                )

                val msg =
                    if (currentModel == LIST_LOOP) getString(R.string.model_normal) else if (currentModel == RANDOM_PATTERN) getString(
                        R.string.model_random
                    ) else getString(R.string.model_repeat)

                msg.showToast(this)
            }
            binding.layoutPlayLayout.ibPlayPreviousSong.id -> {
                intent.putExtra(EXTRA_CONTROL, PREV)
                Timber.v("???????????????1: %s", PREV)
            }

            binding.layoutPlayLayout.flPlayContainer.id -> intent.putExtra(
                EXTRA_CONTROL,
                PAUSE_PLAYBACK
            )

            binding.layoutPlayLayout.ibPlayNextTrack.id -> {
                intent.putExtra(EXTRA_CONTROL, NEXT)
                Timber.v("???????????????1: %s", NEXT)
            }
            binding.layoutPlayLayout.ibMusicList.id -> {
                newInstance()
                    .show(supportFragmentManager, PlayActivityDialogFragment::class.java.simpleName)
            }
        }

        BroadcastUtil.sendLocalBroadcast(intent)
    }

    private val observableCurrentSong = ObservableCurrentSong()
    private var recordingCurrentSong: String? = ""

    /**
     * ???????????????
     */
    override fun onMetaChanged() {
        super.onMetaChanged()
        currentSong = getCurrentSong()
        //????????????
        updateTopStatus(currentSong)
        //???????????????
        val temp = getProgress()
        currentTime = if (temp in 1 until duration) temp else 0
        duration = getDuration()
        binding.seekbar.max = duration

        initLyrics()

        //?????????????????????????????????
        observableCurrentSong.nameCurrentSong = currentSong.mixSongID
        if (recordingCurrentSong != currentSong.mixSongID && isUpdateReceiveIntent) {
            //????????????
            if (isPhotoBackground) {
                receiveIntent(currentSong)
            } else {
                showCover()
            }

            recordingCurrentSong = currentSong.mixSongID
        }
        isUpdateReceiveIntent = true


    }

    override fun onServiceConnected(service: MusicService) {
        super.onServiceConnected(service)
        onPlayStateChange()
    }

    //?????????????????????
    override fun onPlayStateChange() {
        super.onPlayStateChange()
        //??????????????????
        val isPlayful = isPlaying()
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

    override fun onPause() {
        //????????????????????????
        handlerRemoveCallbacks()
        finish()

        super.onPause()

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
                    handlerRemoveCallbacks()
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
     * ????????????
     */
    @SuppressLint("CheckResult")
    private fun showCover() {
        //????????????
        val requestOptions = RequestOptions()
        // requestOptions.placeholder(R.drawable.ic_launcher_background)
        RequestOptions.circleCropTransform()
        requestOptions.transform(RoundedCorners(30))

        launchMain {

            val uriID = SongNetwork.songUriID(currentSong.FileHash, "")
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
                val pic = uriID.data.img
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
                            handlerRemoveCallbacks()
                            updateUi(resource, true)


                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
                binding.ivPlayGuide01.visibility = View.VISIBLE
            }
        }


    }


}