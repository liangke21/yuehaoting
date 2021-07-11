package com.example.musiccrawler.musicService.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.Log
import androidx.media.AudioAttributesCompat
import com.example.musiccrawler.R
import com.example.musiccrawler.base.sevice.SmService
import com.example.musiccrawler.util.Constants.MODE_LOOP
import com.example.musiccrawler.util.Constants.MODE_SHUFFLE
import com.example.musiccrawler.data.kugousingle.SongLists
import com.example.musiccrawler.kotlin.lazyMy
import com.example.musiccrawler.musicService.data.KuGouSongMp3
import com.example.musiccrawler.kotlin.showToast
import timber.log.Timber
import com.example.musiccrawler.kotlin.tryLaunch
import com.example.musiccrawler.util.MusicConstant.NEXT
import com.example.musiccrawler.util.MusicConstant.PAUSE_PLAYBACK
import com.example.musiccrawler.util.MusicConstant.PREV
import com.example.musiccrawler.util.BroadcastUtil
import com.example.musiccrawler.util.MusicConstant.ACTION_CMD
import com.example.musiccrawler.util.MusicConstant.EXTRA_CONTROL
import com.example.musiccrawler.util.MusicConstant.EXTRA_SHUFFLE
import com.example.musiccrawler.util.MusicConstant.PLAY_SELECTED_SONG
import com.example.musiccrawler.util.MusicConstant.UPDATE_META_DATA
import com.example.musiccrawler.util.MusicConstant.UPDATE_PLAY_STATE
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/7 13:04
 * 描述:
 */
class MusicService : SmService(), Playback, CoroutineScope by MainScope() {


    /**
     * 播放列队
     */
    var playQueue = PlayQueue()

    /**
     * MediaPlayer 负责歌曲的播放等
     */
    var mediaPlayer = MediaPlayer()

    /**
     * 当前是否获得AudioFocus
     * 是否获取焦点
     */
    private var audioFocus = false

    /**
     * 播放完当前歌曲后是否停止app
     */
    private var pendingClose: Boolean = false

    /**
     * AudioManager
     * 音频管理器
     */
    private val audioManager: AudioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }


    /**
     * 是否正在设置mediaAppLayer的datasource
     * 是否设置媒体媒体播放器的数据源
     */
    private var prepared = false

    /**
     * 设置播放模式并更新下一首歌曲
     */
    var playModel: Int = MODE_LOOP

    /**
     * 初始化工具类
     */
    var myUtil = BroadcastUtil()

    //音频兼容器
    private val audioAttributes = AudioAttributesCompat.Builder().run {
        setUsage(AudioAttributesCompat.USAGE_MEDIA)
        setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
        build()
    }

    /**
     * 播放控制的 Receiver
     */
    private val controlReceiver: ControlReceiver by lazy {
        ControlReceiver()
    }
    /**
     * 当前是否在播放
     */
    private var isPlay:Boolean=false

    /**
     * 获得是否在播放
     */
    val isPlaying:Boolean
    get() = isPlay

    /**
     * 当前播放的歌曲
     */
    val currentSong:SongLists
        get() = playQueue.song


   private val handler =MusicServiceHandler(this,object :MusicServiceHandler.MusicServiceHandlerData{
       override val playQueueSong: SongLists
           get() = playQueue.song
   })

    /**
     * 播放暂停音量控制器
     */
    private val playPauseVolumeController:PlayPauseVolumeController by lazyMy {
        PlayPauseVolumeController(this)
    }
//_______________________________________|生命周期|______________________________________________________________________________________________________
    private val musicBinder = MusicBinder()

    override fun onBind(intent: Intent): IBinder? {
        return musicBinder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("我再后台运行")
        setUp()

    }
//_______________________________________||______________________________________________________________________________________________________
    private fun setUp() {
        myUtil.registerLocalReceiver(controlReceiver, IntentFilter(ACTION_CMD))
        setUpPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val control = intent?.getIntExtra(EXTRA_CONTROL, -1)
        val action = intent?.action
        Timber.v("onStartCommand, control: $control action: $action flags: $flags startId: $startId")
        tryLaunch {
            delay(200)
            handleStartCommandIntent(intent, action)
        }
        return START_NOT_STICKY
    }

    private fun handleStartCommandIntent(intent: Intent?, action: String?) {

    }
//_____________________________________________________________________________________________________________________________________________

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }
//_____________________________________________________________________________________________________________________________________________

    /**
     * 设置播放
     */
    private fun setPlay(isPlay:Boolean){
        this.isPlay=isPlay
        Timber.v("isPlay是否播放  setPlay()函数 : %s", "isPlaying: $isPlaying  isPlay: $isPlay")
        handler.sendEmptyMessage(UPDATE_PLAY_STATE)
    }
    /**
     * 设置播放列队
     */
    fun setPlayQueue(newQueryList: List<SongLists>, intent: Intent) {
        Timber.d("后台播放3")
        val shuffle = intent.getBooleanExtra(EXTRA_SHUFFLE, false)
        if (newQueryList.isEmpty()) {
            return
        }
        //设置列队播放
        val equals = newQueryList == playQueue.originalQueue
        if (!equals) {
            playQueue.setPlayQueue(newQueryList)
        }

        if (shuffle) {
            playModel = MODE_SHUFFLE
        }

        handleCommand(intent)
    }
//_____________________________________________________________________________________________________________
    /**
     * 广播监听 上一首 暂停 下一首
     */
    inner class ControlReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.v("播放下一首2: %s", intent)
            handleCommand(intent)
        }
    }

    /**
     * 处理命令
     */
    private fun handleCommand(intent: Intent?) {
        val control = intent?.getIntExtra(EXTRA_CONTROL, -1)
        Timber.v("后台播放4 $control")
        when (control) {
            PLAY_SELECTED_SONG -> {
                Timber.v("后台播放5")
                playSelectSong(intent.getIntExtra(EXTRA_POSITION, -1))
            }
            //播放上一首
           PREV -> {
                Timber.v("播放上一首3: %s", control)
                playPrecious()
            }
            //播放暂停
        PAUSE_PLAYBACK -> {
            Timber.v("暂停播放: %s", control)
            toggle()
            }
            //播放下一首
           NEXT -> {
                Timber.v("播放下一首3: %s", control)
                playNext()
            }
        }
    }

    override fun toggle() {
     Timber.v("toggle: %s",mediaPlayer.isPlaying)
        if(mediaPlayer.isPlaying){
            pause(false)
        }else{
            play(true)
        }
    }

    /**
     * 播放下一首
     */
    override fun playNext() {
        Timber.v("播放下一首4: %s")
        playNextOrPrev(true)
    }

    /**
     * 播放上一首
     */
    override fun playPrecious() {
        Timber.v("播放上一首4: %s",)
        playNextOrPrev(false)
    }

    /**
     * 播放 下一首 或则 上一首
     */
    private fun playNextOrPrev(isNext: Boolean) {
        if (playQueue.size() == 0) {
            getString(R.string.list_is_empty).showToast(this)
            return
        }
        Timber.v("播放下一首")
        if (isNext) {
            Timber.v("播放下一首5: %s")
            playQueue.next()
        } else {
            Timber.v("播放上一首5: %s",)
            playQueue.previous()
        }

        if (playQueue.song == SongLists.SONG_LIST) {
            getString(R.string.song_lose_effect)
            Timber.v("播放下一首: %s", "数据为空")
            return
        }
        Timber.v("播放下一首||播放上一首8: %s", playQueue.song)
        setPlay(false)
        readyToPlay(playQueue.song)
    }

    /**
     * 播放暂停
     */
    override fun pause(updateMediaSessionOnly: Boolean) {
        Timber.v("pause()  播放暂停:%s",isPlaying)
        if (updateMediaSessionOnly){
            //更新锁屏
        }else{
            if (!isPlaying){
                return
            }
            setPlay(false)
            handler.sendEmptyMessage(UPDATE_META_DATA)
            playPauseVolumeController.fadeOut()
        }
    }

    /**
     * 播放选中的歌曲 比如在全部歌曲或者专辑详情里面选中某一首歌曲
     *
     * @param position 播放位置
     */
    override fun playSelectSong(position: Int) {
        Timber.d("后台播放6 播放位置$position")
        playQueue.setPosition(position)

        readyToPlay(playQueue.song)
        Timber.v("准备播放下一首数据")
        playQueue.updateNextSong()


    }

    /**
     * 初始化Mediaplayer
     */
    private fun setUpPlayer() {
        mediaPlayer = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(audioAttributes.unwrap() as AudioAttributes)
        } else {
            mediaPlayer.setAudioStreamType(audioAttributes.legacyStreamType)
        }
        //锁屏休眠继续播放
        //   mediaPlayer.setWakeMode(this,PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer.setOnPreparedListener {

            mediaPlayer.seekTo(0)
            play(false)
        }



        mediaPlayer.setOnErrorListener { mp, what, extra ->
            try {
                mediaPlayer.release()
                Log.e(what.toString(), extra.toString())
                return@setOnErrorListener true
            } catch (e: Exception) {

            }
            false
        }


    }

    /**
     * 播放
     */
    override fun play(fadeIn: Boolean) {

        setPlay(true)

        handler.sendEmptyMessage(UPDATE_META_DATA)

        mediaPlayer.start()

        //渐变
        if (fadeIn) {
           playPauseVolumeController.fadeIn()
        } else {
            playPauseVolumeController.directTo(1f)
        }
    }

    /**
     * 准备播放
     *
     * @param song 播放歌曲的路径
     */

    private fun readyToPlay(song: SongLists, requestFocus: Boolean = true) {

        tryLaunch(block = {
            Timber.v("后台播放8 准备播放: %S ", song)
            if (TextUtils.isEmpty(song.FileHash)) {
                getString(R.string.path_empty).showToast(this)
                return@tryLaunch
            }
            //获取MP3连接
            val mp3Uri = KuGouSongMp3().songIDMp3(song.FileHash)
            val uri: Uri = Uri.parse(mp3Uri)
            mediaPlayer.reset()
            withContext(Dispatchers.IO) {
                mediaPlayer.setDataSource(this@MusicService, uri)

            }

            mediaPlayer.prepareAsync()


        },
            catch = {
                (getString(R.string.play_failed) + it).showToast(this)
            },
            catch2 = {
                it.toString().showToast(this)
            }
        )

    }


    companion object {
        const val TAG_LIFECYCLE = "服务器生命周期"
        const val EXTRA_POSITION = "Position"


    }

}