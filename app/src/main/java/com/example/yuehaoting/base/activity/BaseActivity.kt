package com.example.yuehaoting.base.activity

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import androidx.core.app.ActivityCompat
import com.androidlk.baseactivity.Activity.SmMainActivity
import com.example.yuehaoting.musicpath.service.MusicService
import com.example.yuehaoting.musicpath.service.MusicServiceRemote
import com.example.yuehaoting.musicpath.service.MusicServiceRemote.bindToService
import com.example.yuehaoting.musicpath.util.MyUtil
import timber.log.Timber
import java.lang.ref.WeakReference


/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/10 11:16
 * 描述:
 */
open class BaseActivity : SmMainActivity() {
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
        verifyStoragePermissions(this)
    }

    //先定义
    private val REQUEST_EXTERNAL_STORAGE = 1

    private val PERMISSIONS_STORAGE = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE")

    //然后通过一个函数来申请
    fun verifyStoragePermissions(activity: Activity?) {
        try {
            //检测是否有写的权限
            val permission = ActivityCompat.checkSelfPermission(activity!!,
                "android.permission.WRITE_EXTERNAL_STORAGE")
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                 this@BaseActivity.onServiceConnected(musicService)
                Timber.tag(TAG).v("前台服务连接3,app isn't on foreground")
            }

            override fun onServiceDisconnected(name: ComponentName) {
                   this@BaseActivity.onServiceDisConnected()
            }
        })
    }

    private fun onServiceDisConnected() {

    }

    private fun onServiceConnected(musicService: MusicService) {
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