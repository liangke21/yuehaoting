package com.example.musiccrawler.playInterface.activity

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
import com.bumptech.glide.request.target.*
import com.bumptech.glide.request.transition.Transition
import com.example.musiccrawler.R
import com.example.musiccrawler.base.activity.PlayBaseActivity
import com.example.musiccrawler.data.kugousingle.SongLists
import com.example.musiccrawler.databinding.PlayActivityBinding
import com.example.musiccrawler.kotlin.getSp
import com.example.musiccrawler.kotlin.lazyMy
import com.example.musiccrawler.kotlin.tryNull
import com.example.musiccrawler.musicService.service.MusicService
import com.example.musiccrawler.musicService.service.MusicServiceRemote.getCurrentSong
import com.example.musiccrawler.util.BroadcastUtil
import timber.log.Timber
import com.example.musiccrawler.musicService.service.MusicServiceRemote.isPlaying
import com.example.musiccrawler.playInterface.activity.SingerPhoto.handlerRemoveCallbacks
import com.example.musiccrawler.playInterface.activity.SingerPhoto.photoCycle
import com.example.musiccrawler.playInterface.activity.SingerPhoto.singerPhotoUrl
import com.example.musiccrawler.playInterface.viewmodel.PlayViewModel
import com.example.musiccrawler.theme.*
import com.example.musiccrawler.util.MusicConstant.ACTION_CMD
import com.example.musiccrawler.util.MusicConstant.BACKGROUND_ADAPTIVE_COLOR
import com.example.musiccrawler.util.MusicConstant.NAME
import com.example.musiccrawler.util.MusicConstant.NEXT
import com.example.musiccrawler.util.MusicConstant.PAUSE_PLAYBACK
import com.example.musiccrawler.util.MusicConstant.PLAYER_BACKGROUND
import com.example.musiccrawler.util.MusicConstant.PREV
import com.example.musiccrawler.util.MusicConstant.SINGER_ID
import com.example.musiccrawler.util.MusicConstant.BACKGROUND_CUSTOM_IMAGE
import com.example.musiccrawler.util.MusicConstant.CURRENT_SONG
import com.example.musiccrawler.util.MusicConstant.EXTRA_CONTROL
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class PlayActivity : PlayBaseActivity() {
    private lateinit var binding: PlayActivityBinding
    private val myUtil = BroadcastUtil()

    private val viewModel by lazyMy { ViewModelProvider(this).get(PlayViewModel::class.java) }
    private val mCacheUrl = CacheUrl()

    private lateinit var playActivityColor: PlayActivityColor

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
            getInt(PLAYER_BACKGROUND, BACKGROUND_ADAPTIVE_COLOR)
        }
    }

    /**
     * 更新封面
     */
    private var isUpdateReceiveIntent by Delegates.notNull<Boolean>()

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
                    })
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
        //初始化字符缓存
        mCacheUrl.init(this)
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
        arrayOf(
            binding.layoutPlayLayout.ibPlayPreviousSong,
            binding.layoutPlayLayout.flPlayContainer,
            binding.layoutPlayLayout.ibPlayNextTrack
        ).forEach {
            it.setOnClickListener(onCtrlClick)
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

    //接收数据
    private fun receiveIntent(currentSong: SongLists) {
        // val singerId = intent.getStringExtra(SINGER_ID)
        val singerId = currentSong.mixSongID
        val list = mCacheUrl.getFromDisk(singerId)
        if (list != null) {
            photoCycle(list, binding.playerContainer, resources, ::updateUi)
        } else {
            Timber.v("歌手id: %S", singerId)

            Glide.with(this).asBitmap()
                .load(R.drawable.youjing)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        binding.playerContainer.background = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

            viewModel.singerId(singerId)
        }
    }

    private fun observeSingerPhotoData() {


        tryNull {
            viewModel.singerIdObservedData.observe(this) {
                //获取图片连接
                val urlList = singerPhotoUrl(it)
                val singerId = intent.getStringExtra(SINGER_ID)
                mCacheUrl.putToDisk(singerId.toString(), urlList)
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

        myUtil.sendLocalBroadcast(intent)
    }

    private val observableCurrentSong = ObservableCurrentSong()
    private var recordingCurrentSong: String? = ""
    override fun onMetaChanged() {
        super.onMetaChanged()
        currentSong = getCurrentSong()
        //更新标题
        updateTopStatus(currentSong)

        observableCurrentSong.nameCurrentSong = currentSong.mixSongID

        if (recordingCurrentSong != currentSong.mixSongID && isUpdateReceiveIntent) {
            //更新封面
            receiveIntent(currentSong)
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
        val isPlay = isPlaying()
        Timber.v("isPlay是否播放: %s", "isPlaying: $isPlaying  isPlay: $isPlay")
        if (isPlaying != isPlay) {
            Timber.v("isPlay不等于:%s", "isPlaying: $isPlaying" + "isPlay: $isPlay")
            updatePlayButton(isPlay)
        }

    }

    /**
     * 更新播放暂停按钮
     */
    private fun updatePlayButton(isPlay: Boolean) {
        isPlaying = isPlay
        binding.layoutPlayLayout.ppvPlayPause.updateStRte(isPlay, true)
    }

    override fun onPause() {
        //结束写真幻影灯片
        handlerRemoveCallbacks()
        super.onPause()

    }


    /**
     * Bitmap 里面获取颜色
     */
    @SuppressLint("CheckResult")
    fun updateUi(bitmap: Bitmap) {

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
                playActivityColor.updateViewsColor(swatch)
            }) { t: Throwable? -> Timber.v(t) }


    }

/*    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                moveTaskToBack(true);
                return true;
            }
        }

        return super.onKeyUp(keyCode, event)
    }*/

}