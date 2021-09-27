package com.example.yuehaoting.musicService.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import androidx.media.AudioAttributesCompat
import androidx.media.session.MediaButtonReceiver
import com.example.yuehaoting.R
import com.example.yuehaoting.base.db.DatabaseRepository
import com.example.yuehaoting.base.log.LogT
import com.example.yuehaoting.base.log.LogT.lll
import com.example.yuehaoting.base.rxJava.RxUtil.applySingleScheduler
import com.example.yuehaoting.base.sevice.SmService
import com.example.yuehaoting.callback.MusicEvenCallback
import com.example.yuehaoting.util.Constants.MODE_LOOP
import com.example.yuehaoting.util.Constants.MODE_SHUFFLE
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.kotlin.*
import com.example.yuehaoting.musicService.data.KuGouSongMp3
import timber.log.Timber
import com.example.yuehaoting.musicService.data.HifIniSongMp3
import com.example.yuehaoting.util.MusicConstant.NEXT
import com.example.yuehaoting.util.MusicConstant.PAUSE_PLAYBACK
import com.example.yuehaoting.util.MusicConstant.PREV
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.ACTION_CMD
import com.example.yuehaoting.util.MusicConstant.EXTRA_CONTROL
import com.example.yuehaoting.util.MusicConstant.EXTRA_PLAYLIST
import com.example.yuehaoting.util.MusicConstant.EXTRA_POSITION
import com.example.yuehaoting.util.MusicConstant.EXTRA_SHUFFLE
import com.example.yuehaoting.util.MusicConstant.HIF_INI
import com.example.yuehaoting.util.Tag.play
import com.example.yuehaoting.util.MusicConstant.KU_GOU
import com.example.yuehaoting.util.MusicConstant.LIST_LOOP
import com.example.yuehaoting.util.MusicConstant.NEW_SONG_KU_GOU
import com.example.yuehaoting.util.MusicConstant.PLAYLIST_CHANGE
import com.example.yuehaoting.util.MusicConstant.PLAY_SELECTED_SONG
import com.example.yuehaoting.util.MusicConstant.RANDOM_PATTERN
import com.example.yuehaoting.util.MusicConstant.UPDATE_META_DATA
import com.example.yuehaoting.util.MusicConstant.UPDATE_PLAY_STATE
import com.example.yuehaoting.util.Tag
import com.example.yuehaoting.util.Tag.songDuration
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/7 13:04
 * 描述:
 */
class MusicService : SmService(), Playback, MusicEvenCallback, CoroutineScope by MainScope() {

    /**
     * 播放列队
     */
    var playQueue = PlayQueue(this)

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
    var playModel: Int = LIST_LOOP
        set(value) {
            Timber.v("修改播放模式:$value")
            val fromShuffleToNone = field == RANDOM_PATTERN
            field = value

            setSp(this,MusicConstant.NAME){
                putInt(MusicConstant.PLAY_MODEL,value)
            }
            if (fromShuffleToNone) {
                playQueue.rePosition()
            }

            playQueue.makeList()
            playQueue.updateNextSong() //释放下一首在内存里的歌
            updateQueueItem()

        }

    /**
     * 加载
     */
    fun load(){

       //用户数据
        playModel= getSp(this,MusicConstant.NAME){
            getInt(MusicConstant.PLAY_MODEL,LIST_LOOP)
        }


    }

    /**
     * MediaSession
     */
    private lateinit var mediaSession: MediaSessionCompat

    /**
     * 更新列表项目
     */
    private fun updateQueueItem() {
        tryLaunch(block = {
            withContext(Dispatchers.IO) {
                val queue = ArrayList(playQueue.playingQueue)
                    .map { song ->
                        return@map MediaSessionCompat.QueueItem(
                            MediaMetadataCompat.Builder()
                                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
                                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.SongName)
                                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.album)
                                .build().description, song.id
                        )
                    }
                Timber.v("updateQueueItem queue: ${queue.size}")

                mediaSession.setQueueTitle(playQueue.song.SongName)
                mediaSession.setQueue(queue)
            }
        },catch={
            it.toString().showToast(this)
            Timber.e(it)
        })

    }

    /**
     * 初始化MediaSession
     */
    private fun setUpSession(){
        val mediaButtonReceiverComponentName = ComponentName(applicationContext,MediaButtonReceiver::class.java)

        val mediaButtonIntent= Intent(Intent.ACTION_MEDIA_BUTTON)

        mediaButtonIntent.component=mediaButtonReceiverComponentName

        val pendingIntent =PendingIntent.getBroadcast(applicationContext,0,mediaButtonIntent,0)

        mediaSession = MediaSessionCompat(applicationContext,"哇咔咔",mediaButtonReceiverComponentName,pendingIntent)

        mediaSession.setCallback(object :MediaSessionCompat.Callback(){


        })
    }

    /**
     * 初始化工具类
     */
    // var myUtil = BroadcastUtil()

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

    private val musicEventReceiver: MusicEventReceiver by lazy {
        MusicEventReceiver()
    }

    /**
     * 当前是否在播放
     */
    var isPlayT: Boolean = false

    fun revisePlaying() {

        isPlayT = false
        Timber.tag(Tag.isPlay).w("修改播放状态:%s,:%s", this.isPlayT, lll())


    }

    /**
     * 获得是否在播放
     */
    val isPlaying: Boolean
        get() = isPlayT

    /**
     * 后台暂停播放控制
     */
    private var isPlayPause = false

    /**
     * 当前播放的歌曲
     */
    val currentSong: SongLists
        get() = playQueue.song

    /**
     * 获取当前歌曲进度
     */
    val progress: Int
        get() {
            try {
                if (prepared) {
                    return mediaPlayer.currentPosition
                }
            } catch (e: IllegalArgumentException) {
                Timber.tag(songDuration).e("获取当前歌曲进度出错 %s", e.printStackTrace())
            }
            return 0
        }

    /**
     * 获取当前歌曲时长
     */
    val duration: Int
        get() = if (prepared) {
            mediaPlayer.duration
        } else 0


    private val handler = MusicServiceHandler(this, object : MusicServiceHandler.MusicServiceHandlerData {
        override val playQueueSong: SongLists
            get() = playQueue.song
    })

    /**
     * 播放暂停音量控制器
     */
    private val playPauseVolumeController: PlayPauseVolumeController by lazyMy {
        PlayPauseVolumeController(this)
    }

    //_______________________________________|生命周期|______________________________________________________________________________________________________
    private val musicBinder = MusicBinder()

    override fun onBind(intent: Intent): IBinder {
        return musicBinder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("我再后台运行")
        load()
        setUp()

    }

    //_______________________________________||______________________________________________________________________________________________________
    private fun setUp() {

        //初始化Receiver
        val eventFilter = IntentFilter()
        eventFilter.addAction(PLAYLIST_CHANGE)
        BroadcastUtil.registerLocalReceiver(musicEventReceiver, eventFilter)

        BroadcastUtil.registerLocalReceiver(controlReceiver, IntentFilter(ACTION_CMD))
        setUpPlayer()
        setUpSession()
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
    private fun setPlay(isPlay: Boolean) {
        this.isPlayT = isPlay
        this.isPlayPause = isPlay
        Timber.tag(Tag.isPlay).v("更新播放状态:%s,传入状态:%s,:%s", this.isPlayT, isPlay, lll())
        handler.sendEmptyMessage(UPDATE_PLAY_STATE)
    }

    /**
     * 设置播放列队
     */
    fun setPlayQueue(newQueryList: List<SongLists>, intent: Intent) {
        Timber.tag(play).v("设置播放列队2:%s", lll())

        //每次播放心歌曲的时候播放状态为false
        isPlayT = false


        Timber.tag(Tag.isPlay).i("初始化播放状态:%s,:%s", this.isPlayT, lll())
        //获取是否随机播放的参数 默认为false
        val shuffle = intent.getBooleanExtra(EXTRA_SHUFFLE, false)
        if (newQueryList.isEmpty()) {
            return
        }
        //设置列队播放
        val equals = newQueryList == playQueue.originalQueue
        if (!equals) {
            //设置播放列队,当加载进来的数据集合和当前列队不同就会从新设置列队
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
        Timber.tag(play).v("处理命令3:命令%s,:%s", control, lll())
        when (control) {
            PLAY_SELECTED_SONG -> {
                Timber.v("后台播放5")
                playSelectSong(intent.getIntExtra(EXTRA_POSITION, -1), intent)
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
        Timber.v("toggle: %s", mediaPlayer.isPlaying)
        if (mediaPlayer.isPlaying) {
            pause(false)
        } else {
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
        Timber.v("播放上一首4: %s")
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
            Timber.v("播放上一首5: %s")
            playQueue.previous()
        }

        if (playQueue.song == SongLists.SONG_LIST) {
            getString(R.string.song_lose_effect)
            Timber.v("播放下一首: %s", "数据为空")
            return
        }
        Timber.v("播放下一首||播放上一首8: %s", playQueue.song)
        Timber.tag(Tag.isPlay).v("后台播放状态:%s,传入状态:%s,播放 下一首 或则 上一首:%s", isPlaying, false, lll())
        setPlay(false)
        readyToPlay(playQueue.song)
    }

    /**
     * 播放暂停
     */
    override fun pause(updateMediaSessionOnly: Boolean) {
        Timber.v("pause()  播放暂停:%s", isPlaying)
        if (updateMediaSessionOnly) {
            //更新锁屏
        } else {
            if (!isPlayPause) {
                return
            }
            Timber.tag(Tag.isPlay).v("后台播放状态:%s,传入状态:%s,播放暂停:%s", isPlaying, false, lll())
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
    override fun playSelectSong(position: Int, intent: Intent?) {
        Timber.tag(play).v("播放选中的歌曲4:位置%s,:%s", position, lll())
        playQueue.setPosition(position)

        readyToPlay(playQueue.song, intent)
        Timber.v("准备播放下一首数据")
        playQueue.updateNextSong()


    }

    /**
     * 初始化Mediaplayer
     */
    private fun setUpPlayer() {
        Timber.tag(play).v("初始化播放器1:%s", lll())
        mediaPlayer = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(audioAttributes.unwrap() as AudioAttributes)
        } else {
            // mediaPlayer.setAudioStreamType(audioAttributes.legacyStreamType)
            mediaPlayer.setAudioAttributes(audioAttributes.unwrap() as AudioAttributes)
        }

        //锁屏休眠继续播放
        //   mediaPlayer.setWakeMode(this,PowerManager.PARTIAL_WAKE_LOCK)
        //准备好监听播放
        mediaPlayer.setOnPreparedListener {

            mediaPlayer.seekTo(0)
            play(false)

        }
        //播放完成
     mediaPlayer.setOnCompletionListener{
        val intent= Intent((ACTION_CMD))
         intent.putExtra(EXTRA_CONTROL, NEXT)
         BroadcastUtil.sendLocalBroadcast(intent)
     }

        //错误监听
        mediaPlayer.setOnErrorListener { _, what, extra ->
            try {
                prepared = false
                mediaPlayer.release()
                setUpPlayer()
                "播放器正在从新初始h化".showToast(this)
                Log.e(what.toString(), extra.toString())
                return@setOnErrorListener true
            } catch (e: Exception) {

            }
            false
        }


    }

    /**
     * 音乐事件
     */
    inner class MusicEventReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            handleMusicEvent(intent)
        }
    }

    private fun handleMusicEvent(intent: Intent?) {
        if (intent == null) {
            return
        }
        when (intent.action) {
            PLAYLIST_CHANGE -> {
                intent.getStringExtra(EXTRA_PLAYLIST)?.let { onPlayListChanged(it) }
            }
        }
    }

    override fun onMediaStoreChanged() {
        TODO("Not yet implemented")
    }

    override fun onPermissionChanged(has: Boolean) {
        TODO("Not yet implemented")
    }

    /**
     * 数据库
     */
    val repository = DatabaseRepository.getInstance()

    /**
     * 列表发生改变
     * @param name String
     */
    @SuppressLint("CheckResult")
    override fun onPlayListChanged(name: String) {
        repository.getPlayQueueSongs()
            .compose(applySingleScheduler())
            .subscribe { songs ->
                if (songs.isEmpty() || songs == playQueue.originalQueue) {
                    Timber.tag("忽略onPlayListChanged")
                    return@subscribe
                }
                Timber.v("新的播放队列: ${songs.size}")

                playQueue.setPlayQueue(songs)

                //todo 随机播放
                /**
                 * 如果下一首歌曲不在队列里面 重新设置下一首歌曲
                 * 此处逻辑 目前 是用于 播放列队 删除歌曲 与后台列队同步.在同步的同时,下一首歌已经在内存中,
                 * 所以要调用一次 更新下一首 来跳过这个首被删除的歌,
                 * 主要逻辑是,列队删除的是下一首歌
                 */
                if (!playQueue.playingQueue.contains(playQueue.nextSong)) {
                    Timber.v("播放队列改变后重新设置下一首歌曲")
                    playQueue.updateNextSong()
                }
            }
    }

    override fun onServiceConnected(service: MusicService) {
        TODO("Not yet implemented")
    }

    override fun onMetaChanged() {
        TODO("Not yet implemented")
    }

    override fun onPlayStateChange() {
        TODO("Not yet implemented")
    }

    override fun onServiceDisConnected() {
        TODO("Not yet implemented")
    }

    override fun onTagChanged(oldSong: SongLists, newSongLists: SongLists) {
        TODO("Not yet implemented")
    }

    /**
     * 播放
     */
    override fun play(fadeIn: Boolean) {
        Timber.tag(play).v("播放6:%s", lll())
        Timber.tag(Tag.isPlay).v("后台播放状态:%s,传入状态:%s,播放:%s", isPlaying, true, lll())
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
     * 设置MediaPlayer播放进度
     */
    fun setProgressL(position: Int) {
        if (prepared) {
            mediaPlayer.seekTo(position)
        }
    }

    /**
     * 准备播放
     *
     * @param song 播放歌曲的路径
     */

    private fun readyToPlay(song: SongLists, intent: Intent? = null) {
        Timber.tag(play).v("歌曲信息添加进播放器5:%s", lll())
        tryLaunch(block = {
            val ent = song.platform
            Timber.v("KEY_MUSIC_PLATFORM:%s", ent)
            prepared = false

            when (ent) {
                KU_GOU -> {
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

                }

                HIF_INI -> {
                    if (TextUtils.isEmpty(song.FileHash)) {
                        getString(R.string.path_empty).showToast(this)
                        return@tryLaunch
                    }
                    //获取MP3连接
                    val mp3Uri = HifIniSongMp3.songIDMp3(song.FileHash)
                    val uri: Uri = Uri.parse(mp3Uri)
                    mediaPlayer.reset()
                    withContext(Dispatchers.IO) {
                        mediaPlayer.setDataSource(this@MusicService, uri)

                    }

                }
                NEW_SONG_KU_GOU -> {
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


                }


            }


            mediaPlayer.prepareAsync()
            prepared = true

        },
            catch = {
                (getString(R.string.play_failed) + it).showToast(this)
                prepared = false
            },
            catch2 = {
                it.toString().showToast(this)
                prepared = false
            }
        )

    }


    companion object {
        const val TAG_LIFECYCLE = "服务器生命周期"


    }

}