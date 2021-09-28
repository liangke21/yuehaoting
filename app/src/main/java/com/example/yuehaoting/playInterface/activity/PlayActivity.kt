package com.example.yuehaoting.playInterface.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.*
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.App
import com.example.yuehaoting.App.Companion.context
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.PlayBaseActivity
import com.example.yuehaoting.base.diskLruCache.myCache.CacheString
import com.example.yuehaoting.base.diskLruCache.myCache.CacheUrl
import com.example.yuehaoting.base.log.LogT
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.PlayActivityBinding
import com.example.yuehaoting.kotlin.*
import com.example.yuehaoting.lyrics.LyricsReader
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
import java.io.File
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
        getSp(this, NAME) {
            getInt(PLAYER_BACKGROUND, BACKGROUND_CUSTOM_IMAGE)
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

    private val handler: PlayActivityHandler by lazyMy {
        PlayActivityHandler(binding, this)
    }

    override fun setSatuBarColor() {
        when (background) {
            // 背景自适应  更是封面
            BACKGROUND_ADAPTIVE_COLOR -> {
                StatusBarUtil.setTransparent(this)
                Timber.v("播放界面状态栏背景 背景自适应  更是封面 : %s", background)
            }
            //背景图片定义
            BACKGROUND_CUSTOM_IMAGE -> {
                StatusBarUtil.setTransparent(this)

                Glide.with(this).asBitmap()
                    .load(R.drawable.youjing)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
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
        //初始化字符集合缓存
        mCacheUrl.init(this)
        //字符集合
        mCacheString.init(this, "Cover")
        //初始化ActivityColor
        playActivityColor = PlayActivityColor(binding, this)

        receiveIntent(currentSong)
        isUpdateReceiveIntent = false

        observeSingerPhotoData()

        playActivityColor.setThemeColor()

        initView()
        updateTopStatus(currentSong)

        seekBarRenew()

        initLyrics()
    }

    /**
     * 歌词解析器
     */
    private fun initLyrics() {
       val mLyricsReader= LyricsReader()
        val lyrics="[00:17.87]F:共你相识三千天 我没名无姓\n[00:21.26]庆幸也与你逛过 那一段旅程\n[00:25.09]曾是日夜期待你 施舍一点同情\n[00:29.06]我对你是固执 做梦或太热情\n[00:32.94]S:在世上 是你始终不肯退后遗忘我\n[00:37.42]感激你心意 但情人比\n[00:41.13]知己分开更易 怕我爱上你坏了事\n[00:45.92]F:完了吧 如无意外\n[00:49.43]从今开始该好好恋爱\n[00:53.09]放下从前一段感情\n[00:55.69]才能追求将来 你就似没存在\n[01:00.20]S:完了吧 然而你不在\n[01:03.75]情况未像幻想般变改\n[01:07.28]告別从前总是不易\n[01:09.98]原来假如只得我在\n[01:13.11]我竟未能觅寻下一位挚爱\n[01:23.81]F:旧信息应该删走 再没留凭证\n[01:27.38]我共你去到最远 也只是友情\n[01:31.08]如现实是场玩笑 一早清楚内情\n[01:34.93]过去是勇敢 或是未肯适应\n[01:38.97]S:是我笨\n[01:40.12]大概必须先经错误才能会分清我心意\n[01:45.69]共行成长 数不清的故事\n[01:49.59]S:我已爱上你坏了事(F:我爱你你扮作不知)\n[01:52.24]F:完了吧 如无意外(S:早该放开 重有感慨)\n[01:55.85]F:从今开始该好好恋爱(S:难道我寂寞不来)\n[01:59.46]F:放下从前一段感情\n[02:02.09]F:才能追求将来 你就似没存在(S当做我没存在)\n[02:06.60]S:完了吧(F:应该放开)\n[02:08.20]S:仍能撑起来(F:纵有感慨)\n[02:10.19]S:前进便让自尊心放开(F:期望你能寻觅爱)\n[02:13.75]S:告別从前总是不易\n[02:16.46]S:然而假如不止你在(F:只得我在)\n[02:19.60]你可愿仍逗（A：再不愿盲目）合：留在这爱海\n[02:25.82]F:我与你 大概始终不能相爱\n[02:29.44]S:可否不离开 讲出你的感慨\n[02:34.73]S:你用心恋爱(F:我用心恋爱)\n[02:36.16]合:下段道路定更精彩\n[02:40.93]F:完了吧 如无意外\n[02:44.21]曾失恋的 都必須恋爱\n[02:47.80]S:悔恨从前隐瞒感情 常常猜疑将来\n[02:52.31]S:我就似没存在(F:你就似没存在)\n[02:54.96]合:完了吧 仍能撑起来\n[02:58.66]前进便让自尊心放开\n[03:02.23]F:告別从前总是不易\n[03:04.81]F:然而假如只得我在(S:然而假如不止你在)\n[03:07.97]F:我怎样来觅寻下一位挚爱 （S:你可愿停下来望清这挚爱）"
        val file = File(context.externalCacheDir.toString() +File.separator+ "lyrics"+File.separator+"aa.lrcwy")
        mLyricsReader.readLrcText(lyrics,file)
       binding.ManyLyricsView.initLrcData()
       binding.ManyLyricsView.lyricsReader = mLyricsReader

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
        duration = getDuration()
        val temp = getProgress()
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
    }

    /**
     * 更新进度条线程
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


    //初始化控件
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

    }

    override fun onResume() {
        super.onResume()
        ProgressThread().start()
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

    /**
     * 接收数据
     * 2325 表示歌曲写真id来自 HifIni
     */
    private fun receiveIntent(currentSong: SongLists) {
        // val singerId = intent.getStringExtra(SINGER_ID)
        val singerId = currentSong.mixSongID
        if (singerId != "2325") {
            val list = mCacheUrl.getFromDisk(singerId)
            Timber.v("歌手写真url缓存文件:%s", list?.size)
            if (list != null) {
                photoCycle(list, binding.playerContainer, resources, ::updateUi)
            } else {
                Timber.v("歌手id: %S", singerId)

                Glide.with(App.context).asBitmap()
                    .load(R.drawable.youjing)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            binding.playerContainer.background = BitmapDrawable(resources, resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })

                viewModel.singerId(singerId)
            }

        } else {
            Glide.with(App.context).asBitmap()
                .load(R.drawable.youjing)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        binding.playerContainer.background = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    private fun observeSingerPhotoData() {


        tryNull {
            viewModel.singerIdObservedData.observe(this) {
                //获取图片连接
                val urlList = singerPhotoUrl(it)
                val singerId = currentSong.mixSongID
                mCacheUrl.putToDisk(singerId, urlList)
                //把图片设置为背景
                photoCycle(urlList, binding.playerContainer, resources, ::updateUi)
            }
        }

    }

    /**
     *   上一首 播放暂停 下一首
     */
    private val onCtrlClick = View.OnClickListener { v ->
        val intent = Intent(ACTION_CMD)
        when (v.id) {
            binding.layoutPlayLayout.ibPlayPlayMode.id -> {
                var currentModel = getPlayModel()

                Timber.v("播放模式 %s", currentModel)


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

                val msg = if (currentModel == LIST_LOOP) getString(R.string.model_normal) else if (currentModel == RANDOM_PATTERN) getString(R.string.model_random) else getString(R.string.model_repeat)

                msg.showToast(this)
            }
            binding.layoutPlayLayout.ibPlayPreviousSong.id -> {
                intent.putExtra(EXTRA_CONTROL, PREV)
                Timber.v("播放上一首1: %s", PREV)
            }

            binding.layoutPlayLayout.flPlayContainer.id -> intent.putExtra(EXTRA_CONTROL, PAUSE_PLAYBACK)

            binding.layoutPlayLayout.ibPlayNextTrack.id -> {
                intent.putExtra(EXTRA_CONTROL, NEXT)
                Timber.v("播放下一首1: %s", NEXT)
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
     * 元数据更改
     */
    override fun onMetaChanged() {
        super.onMetaChanged()
        currentSong = getCurrentSong()
        //更新标题
        updateTopStatus(currentSong)
        //更新进度条
        val temp = getProgress()
        currentTime = if (temp in 1 until duration) temp else 0
        duration = getDuration()
        binding.seekbar.max = duration

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


    }

    override fun onServiceConnected(service: MusicService) {
        super.onServiceConnected(service)
        onPlayStateChange()
    }

    //播放状态已更改
    override fun onPlayStateChange() {
        super.onPlayStateChange()
        //更新按钮状态
        val isPlayful = isPlaying()
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
        Timber.tag(Tag.isPlay).v("======================================================================:%s", currentSong.SongName)
    }

    override fun onPause() {
        //结束写真幻影灯片
        handlerRemoveCallbacks()
        finish()

        super.onPause()

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
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
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
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            binding.ivPlayGuide01.setImageBitmap(resource)
                            //结束写真幻影灯片
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