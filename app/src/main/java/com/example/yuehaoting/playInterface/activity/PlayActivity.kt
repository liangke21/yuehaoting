package com.example.yuehaoting.playInterface.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Bundle
import android.view.View
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
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.PlayActivityBinding
import com.example.yuehaoting.kotlin.*
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.musicService.service.MusicServiceRemote.getCurrentSong
import com.example.yuehaoting.musicService.service.MusicServiceRemote.isPlaying
import com.example.yuehaoting.musicService.service.MusicServiceRemote.revisePlaying
import com.example.yuehaoting.playInterface.activity.SingerPhoto.handlerRemoveCallbacks
import com.example.yuehaoting.playInterface.activity.SingerPhoto.photoCycle
import com.example.yuehaoting.playInterface.activity.SingerPhoto.singerPhotoUrl
import com.example.yuehaoting.playInterface.framelayou.PlayPauseView
import com.example.yuehaoting.playInterface.viewmodel.PlayViewModel
import com.example.yuehaoting.theme.*
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.MusicConstant.ACTION_CMD
import com.example.yuehaoting.util.MusicConstant.BACKGROUND_ADAPTIVE_COLOR
import com.example.yuehaoting.util.MusicConstant.BACKGROUND_CUSTOM_IMAGE
import com.example.yuehaoting.util.MusicConstant.CURRENT_SONG
import com.example.yuehaoting.util.MusicConstant.EXTRA_CONTROL
import com.example.yuehaoting.util.MusicConstant.NAME
import com.example.yuehaoting.util.MusicConstant.NEXT
import com.example.yuehaoting.util.MusicConstant.PAUSE_PLAYBACK
import com.example.yuehaoting.util.MusicConstant.PLAYER_BACKGROUND
import com.example.yuehaoting.util.MusicConstant.PREV
import com.example.yuehaoting.util.Tag
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class PlayActivity : PlayBaseActivity(), View.OnClickListener {
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
    }

    //初始化控件
    private fun initView() {
        binding.ivPlayGuide01.visibility = View.GONE

        ppvPlayPause = findViewById(R.id.ppv_play_pause)

        binding.layoutPlayLayout.ibPlayPlayMode.setOnClickListener(this)
        arrayOf(
            binding.layoutPlayLayout.ibPlayPreviousSong,
            binding.layoutPlayLayout.flPlayContainer,
            binding.layoutPlayLayout.ibPlayNextTrack
        ).forEach {
            it.setOnClickListener(onCtrlClick)
        }

        binding.llPlayIndicator.setOnClickListener(this)


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
     * 播放上一首 暂停 下一首
     */
    private val onCtrlClick = View.OnClickListener { v ->
        val intent = Intent(ACTION_CMD)
        when (v.id) {
            R.id.ib_play_Previous_song -> {
                intent.putExtra(EXTRA_CONTROL, PREV)
                Timber.v("播放上一首1: %s", PREV)
            }

            R.id.fl_play_container -> intent.putExtra(EXTRA_CONTROL, PAUSE_PLAYBACK)

            R.id.ib_play_next_track -> {
                intent.putExtra(EXTRA_CONTROL, NEXT)
                Timber.v("播放下一首1: %s", NEXT)
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
        //封面图片旋转
        // binding.ivPlayGuide01.setRotate(isPlay)
        revisePlaying()
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
            binding.layoutPlayLayout.ibPlayPlayMode.id -> {

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