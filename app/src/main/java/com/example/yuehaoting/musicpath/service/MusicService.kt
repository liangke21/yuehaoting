package com.example.yuehaoting.musicpath.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.yuehaoting.R
import com.example.yuehaoting.musicpath.service.Command.Companion.PLAYSONG
import com.example.yuehaoting.musicpath.util.Constants.MODE_LOOP
import com.example.yuehaoting.musicpath.util.Constants.MODE_SHUFFLE
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.musicpath.data.KuGouSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import com.example.yuehaoting.musicpath.tryLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/7 13:04
 * 描述:
 */
class MusicService : Service(), CoroutineScope by MainScope() {

    /**
     * 播放列队
     */
    var playQueue = PlayQueue()

    /**
     * MediaPlayer 负责歌曲的播放等
     */
    var mediaPlayer: MediaPlayer = MediaPlayer()

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
     * 是否正在设置mediapplayer的datasource
     * 是否设置媒体媒体播放器的数据源
     */
    private var prepared = false

    /**
     * 设置播放模式并更新下一首歌曲
     */
    var playModel: Int = MODE_LOOP

    /**
     * 当前是否正在播放
     */
    private var isPlay: Boolean = false

    private val musicBinder = MusicBinder()

    override fun onBind(intent: Intent): IBinder? {
        return musicBinder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("我再后台运行")

    }

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
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

    private fun handleCommand(intent: Intent) {
        val control = intent.getIntExtra(EXTRA_CONTROL, -1)
        Timber.d("后台播放4 $control")
        when (control) {
            PLAYSONG -> {
                Timber.d("后台播放5")
                playSelectSong(intent.getIntExtra(EXTRA_POSITION, -1))
            }
        }
    }

    /**
     * 播放选中的歌曲 比如在全部歌曲或者专辑详情里面选中某一首歌曲
     *
     * @param position 播放位置
     */
    private fun playSelectSong(position: Int) {
        Timber.d("后台播放6 播放位置$position")
        playQueue.setPosition(position)
        readyToPlay(playQueue.song)
    }


    /**
     * 准备播放
     *
     * @param song 播放歌曲的路径
     */

    private fun readyToPlay(song: SongLists, requestFocus: Boolean = true) {

        tryLaunch (block = {
            Timber.v("后台播放8 准备播放", song.toString())
            if (TextUtils.isEmpty(song.FileHash)) {
                Toast.makeText(this, R.string.path_empty, Toast.LENGTH_SHORT).show()
                return@tryLaunch
            }


            KuGouSong().songID(song.FileHash)
            val uri: Uri = Uri.parse("")
            withContext(Dispatchers.IO) {
                mediaPlayer.setDataSource(this@MusicService, uri)

            }

        } ,
         catch = {

         }
        )
    }


    companion object {
        const val TAG_LIFECYCLE = "服务器生命周期"
        const val EXTRA_POSITION = "Position"

        //更新桌面部件
        const val UPDATE_APPWIDGET = 1000

        //更新正在播放歌曲
        const val UPDATE_META_DATA = 1002

        //更新播放状态
        const val UPDATE_PLAY_STATE = 1003

        //更新桌面歌词内容
        const val UPDATE_DESKTOP_LRC_CONTENT = 1004

        //移除桌面歌词
        const val REMOVE_DESKTOP_LRC = 1005

        //添加桌面歌词
        const val CREATE_DESKTOP_LRC = 1006

        //更新状态栏歌词
        const val UPDATE_STATUS_BAR_LRC = 1007

        //更新通知
        const val UPDATE_NOTIFICATION = 1008

        //包名
        const val PACKAGE_NAME = "com.example.yuehaoting"

        //媒体数据库变化
        const val MEDIA_STORE_CHANGE = "$PACKAGE_NAME.media_store.change"

        //读写权限变化
        const val PERMISSION_CHANGE = "$PACKAGE_NAME.permission.change"

        //播放列表变换
        const val PLAYLIST_CHANGE = "$PACKAGE_NAME.playlist.change"

        //播放数据变化
        const val PLAY_DATA_CHANGES = "$PACKAGE_NAME.meta.change"

        //播放状态变化
        const val PLAY_STATE_CHANGE = "$PACKAGE_NAME.play_state.change"

        //歌曲标签变化
        const val TAG_CHANGE = "$PACKAGE_NAME.tag_change"

        //操作命令
        const val ACTION_COM = "$PACKAGE_NAME.cmd"

        //额外控制
        const val EXTRA_CONTROL = "Control"

        const val EXTRA_SHUFFLE = "shuffle"

    }

}