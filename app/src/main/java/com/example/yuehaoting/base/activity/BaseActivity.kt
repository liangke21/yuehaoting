package com.example.yuehaoting.base.activity

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import com.androidlk.baseactivity.Activity.SmMainActivity
import com.example.yuehaoting.callback.MusicEvenCallback
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.musicService.service.MusicServiceRemote.bindToService
import com.example.yuehaoting.util.MyUtil
import timber.log.Timber
import java.lang.ref.WeakReference


/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/10 11:16
 * 描述:
 */
open class BaseActivity : SmMainActivity(),MusicEvenCallback {
    private var TAG = this::class.java.simpleName
    private var util = MyUtil()

    private var serviceToken: MusicServiceRemote.ServiceToken? = null

    //待绑定的服务
    private var pendingBindService = false

    //音乐处理状态
    private var musicStateHandler: MusicStatHandler? = null

    //广播接收者
    private var receiverRegistered: Boolean = false

    //音频接收器
    private var musicStateReceiver: MusicStatReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binToService()
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(TAG).v("onResume")
        if (pendingBindService) {
            binToService()
        }
    }

    /**
     * Activity传递消息 Service
     */
    private fun binToService() {
        if (!util.isAppOnForeground()) {
            Timber.tag(TAG).v("bindToService(),app isn't on foreground APP 不在前台")
            pendingBindService = false
            return
        }
        serviceToken = bindToService(this, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val musicService = (service as MusicService.MusicBinder).service
                Timber.tag(TAG).v("前台服务连接3,app isn't on foreground")
                 this@BaseActivity.onServiceConnected(musicService)

            }

            override fun onServiceDisconnected(name: ComponentName) {
                   this@BaseActivity.onServiceDisConnected()
            }
        })
    }


    //歌曲标签发生变化
    override fun onTagChanged(oldSong: SongLists, newSongLists: SongLists) {
        TODO("Not yet implemented")
    }
    //媒体商店的变化
    override fun onMediaStoreChanged() {
        TODO("Not yet implemented")
    }
    //权限变更
    override fun onPermissionChanged(has: Boolean) {
        TODO("Not yet implemented")
    }
    //播放列表变化
    override fun onPlayListChanged(name: String) {
        TODO("Not yet implemented")
    }
    //播放状态变化
    override fun onPlayStateChange() {
        TODO("Not yet implemented")
    }


    override fun onServiceConnected(musicService: MusicService) {
        Timber.tag(TAG).v("服务连接上2,$musicService")
        if (!receiverRegistered) {
            musicStateReceiver = MusicStatReceiver(this)
            val filter = IntentFilter()
            filter.addAction(MusicService.PLAYLIST_CHANGE) //播放列表变化
            filter.addAction(MusicService.PERMISSION_CHANGE) //权限变更
            filter.addAction(MusicService.MEDIA_STORE_CHANGE) //媒体商店的变化
            filter.addAction(MusicService.PLAY_DATA_CHANGES) //播放时数据变化
            filter.addAction(MusicService.PLAY_STATE_CHANGE) //播放状态变化
            filter.addAction(MusicService.TAG_CHANGE)//歌曲标签发生变化
            registerReceiver(musicStateReceiver,filter)
            receiverRegistered =true
        }

    }

    override fun onServiceDisConnected() {

    }

    override fun onMetaChanged() {
        TODO("Not yet implemented")
    }

    private class MusicStatHandler(activity: BaseActivity) : Handler() {
        private val ref: WeakReference<BaseActivity> = WeakReference(activity)
        override fun dispatchMessage(msg: Message) {
            super.dispatchMessage(msg)
            val action = msg.obj.toString()
            val activity = ref.get()
            if (action != null && activity != null) {
                when (action) {

                }
            }

        }
    }

    /**
     * 动态太监听广播
     */
    private class MusicStatReceiver(activity: BaseActivity) : BroadcastReceiver() {
        private val ref: WeakReference<BaseActivity> = WeakReference(activity)
        override fun onReceive(context: Context, intent: Intent) {
            ref.get()?.musicStateHandler?.let {
                val action = intent.action
                val msg = it.obtainMessage(action.hashCode())
                msg.obj = action
                msg.data = intent.extras
                it.removeMessages(msg.what)
                it.sendMessageDelayed(msg, 50)
            }

        }
    }
}