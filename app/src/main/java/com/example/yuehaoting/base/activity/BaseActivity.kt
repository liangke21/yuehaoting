package com.example.yuehaoting.base.activity

import android.annotation.SuppressLint
import android.content.*
import android.os.*
import android.view.View
import android.widget.TextView
import com.example.yuehaoting.R
import com.example.yuehaoting.callback.MusicEvenCallback
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.musicService.service.MusicServiceRemote.bindToService
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.MusicConstant.EXTRA_PLAYLIST
import com.example.yuehaoting.util.MusicConstant.MEDIA_STORE_CHANGE
import com.example.yuehaoting.util.MusicConstant.PERMISSION_CHANGE
import com.example.yuehaoting.util.MusicConstant.PLAYLIST_CHANGE
import com.example.yuehaoting.util.MusicConstant.PLAY_DATA_CHANGES
import com.example.yuehaoting.util.MusicConstant.PLAY_STATE_CHANGE
import com.example.yuehaoting.util.MusicConstant.TAG_CHANGE
import com.example.yuehaoting.util.Tag
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*


/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/10 11:16
 * 描述:
 */
open class BaseActivity : SmMainActivity(), MusicEvenCallback, CoroutineScope by MainScope() {
    private var TAG = this::class.java.simpleName
    // private var util = BroadcastUtil()
    //  private var myUtil = BroadcastUtil()
    /**
     * 用于是否更新歌曲时间进度条
     */
    protected var isForeground = false

    private var serviceToken: MusicServiceRemote.ServiceToken? = null

    //待绑定的服务
    private var pendingBindService = false

    //音乐处理状态
    private var musicStateHandler: MusicStatHandler? = null

    //广播接收者
    private var receiverRegistered: Boolean = false

    //音频接收器
    private var musicStateReceiver: MusicStatReceiver? = null

    //服务监听事件
    private val serviceEventListeners = ArrayList<MusicEvenCallback>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binToService()
        testAppNotify()
    }

    override fun onResume() {
        super.onResume()
        isForeground = true
        Timber.tag(TAG).v("onResume")
        if (pendingBindService) {
            binToService()
        }
    }

    /**
     * 添加要实现的接口
     * @param listener MusicEvenCallback? 实现接口的类
     */
    fun addMusicServiceEventListener(listener: MusicEvenCallback?) {
        if (listener != null) {
            serviceEventListeners.add(listener)
        }
    }

    /**
     * 移除实现的接口
     * @param listener MusicEvenCallback? 实现接口的类
     */
    fun removeMusicServiceEventListener(listener: MusicEvenCallback?) {
        if (listener != null) {
            serviceEventListeners.remove(listener)
        }
    }

    /**
     * Activity传递消息 Service
     */
    private fun binToService() {
        if (!BroadcastUtil.isAppOnForeground()) {
            Timber.tag(TAG).v("bindToService(),app isn't on foreground APP 不在前台")
            pendingBindService = true
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

        pendingBindService = false
    }


    //歌曲标签发生变化
    override fun onTagChanged(oldSong: SongLists, newSongLists: SongLists) {
        TODO("Not yet implemented")
    }

    //媒体商店的变化
    override fun onMediaStoreChanged() {

    }

    //权限变更
    override fun onPermissionChanged(has: Boolean) {
        TODO("Not yet implemented")
    }

    //播放列表变化
    override fun onPlayListChanged(name: String) {
        for (listener in serviceEventListeners) {
            listener.onPlayListChanged(name)
        }
    }

    /**
     * 播放状态发生变化
     */
    override fun onPlayStateChange() {
        for (listener in serviceEventListeners) {
            listener.onPlayStateChange()
        }
    }

    /**
     * 播放数据发生变化
     */
    override fun onMetaChanged() {
        for (listener in serviceEventListeners) {
            listener.onMetaChanged()
        }
    }

    /**
     * 每个Activity连接都会执行onServiceConnected
     * if(TAG =="PlayActivity") 是判断这个活动可以注册广播
     * 有其他活动,后期加判断,
     */
    override fun onServiceConnected(service: MusicService) {
        Timber.tag(TAG).v("服务连接上2,${service}  $receiverRegistered  $TAG")

        //    if(TAG =="PlayActivity") {
        if (!receiverRegistered) {
            musicStateReceiver = MusicStatReceiver(this)
            Timber.tag(Tag.Broadcast).v("MusicStatReceiver(this): %s", musicStateReceiver.toString())
            val filter = IntentFilter()
            filter.addAction(PLAYLIST_CHANGE) //播放列表变化
            filter.addAction(PERMISSION_CHANGE) //权限变更
            filter.addAction(MEDIA_STORE_CHANGE) //媒体商店的变化
            filter.addAction(PLAY_DATA_CHANGES) //播放时数据变化
            filter.addAction(PLAY_STATE_CHANGE) //播放状态变化
            filter.addAction(TAG_CHANGE)//歌曲标签发生变化
            BroadcastUtil.registerLocalReceiver(musicStateReceiver!!, filter)
            receiverRegistered = true
        }
        for (listener in serviceEventListeners) {
            listener.onServiceConnected(service)
        }
        musicStateHandler = MusicStatHandler(this)
        //   }

    }

    @SuppressLint("HandlerLeak")
    private inner class MusicStatHandler(activity: BaseActivity) : Handler(Looper.getMainLooper()) {
        private val ref: WeakReference<BaseActivity> = WeakReference(activity)
        override fun dispatchMessage(msg: Message) {
            val action = msg.obj.toString()
            val activity = ref.get()
            if (activity != null) {
                when (action) {
                    PLAY_STATE_CHANGE -> {
                        activity.onPlayStateChange()
                        Timber.v("isPlay是否播放   播放回调: %s 当前活动 %s ", action, TAG)
                    }
                    PLAY_DATA_CHANGES -> activity.onMetaChanged()

                    PLAYLIST_CHANGE -> {
                        msg.data.getString(EXTRA_PLAYLIST)?.let { activity.onPlayListChanged(it) }
                    }
                }
            }
        }
    }

    /**
     * 动态太监听广播
     */
    private inner class MusicStatReceiver(activity: BaseActivity) : BroadcastReceiver() {
        private val ref: WeakReference<BaseActivity> = WeakReference(activity)

        override fun onReceive(context: Context, intent: Intent) {
            Timber.tag(Tag.Broadcast).v("接收广播: %s 当前活动 %s", intent.action, TAG)
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


    override fun onServiceDisConnected() {
        if (receiverRegistered) {
            BroadcastUtil.unregisterLocalReceiver(musicStateReceiver!!)
        }
        musicStateHandler?.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        super.onPause()
        isForeground = false
    }

    override fun onDestroy() {
        super.onDestroy()
        //重点,每次销毁Activity,注销广播
        cancel()
        MusicServiceRemote.unbindFromService(serviceToken)
        serviceEventListeners.clear()
        musicStateHandler = null
        musicStateHandler = null
        serviceToken = null
        if (receiverRegistered) {
            BroadcastUtil.unregisterLocalReceiver(musicStateReceiver!!)
            receiverRegistered = true
        }

    }


    fun getForeground(): Boolean {
        return isForeground
    }

    /**
     * 测试APP通知
     */
    private fun testAppNotify() {

        launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://55-1251889734.cos.ap-beijing-1.myqcloud.com/app/yuehaoting/upDate.json")
                .build()
            val response = client.newCall(request).execute()
            val requestData = response.body()?.string()
            val json = JSONObject(requestData.toString())
            val turnOn = json.getString("TurnOn")
            if (!turnOn.toBoolean()) {
                return@launch
            }
            val title = json.getString("Title")
            val message = json.getString("Message")
            val text = json.getString("Text")


            MessageDialog.show(title, message)
                .setCustomView(object : OnBindView<MessageDialog?>(R.layout.custom_view_1) {
                    override fun onBind(dialog: MessageDialog?, v: View?) {
                        val view = v?.findViewById<TextView>(R.id.tv_TextView)
                        view?.text = text
                    }
                }).isCancelable = false

        }

        val millis = System.currentTimeMillis()

        val cal = Calendar.getInstance()
        cal.set(2021, 10, 14, 0, 0,0)

         if (millis>=cal.time.time){
             MessageDialog.show("[乐好听] 学习测试", "该APP,只用于学习,请大家支持正版,")
                 .setCustomView(object : OnBindView<MessageDialog?>(R.layout.custom_view_1) {
                     override fun onBind(dialog: MessageDialog?, v: View?) {
                         val view  =v?.findViewById<TextView>(R.id.tv_TextView)
                         view?.text = "谢谢大家!"
                     }
                 }).isCancelable = false
         }

    }


}