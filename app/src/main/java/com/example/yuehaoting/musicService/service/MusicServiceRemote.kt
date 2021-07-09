package com.example.yuehaoting.musicService.service

import android.app.Activity
import android.content.*
import android.os.IBinder
import com.example.yuehaoting.data.kugousingle.SongLists
import timber.log.Timber
import java.util.*

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 13:49
 * 描述:  音乐后台遥控器
 */
object MusicServiceRemote {
    val TAG = MusicServiceRemote::class.java.simpleName

    @JvmStatic
    var service: MusicService? = null

    private val connectionMap = WeakHashMap<Context, ServiceBinder>()

    @JvmStatic
    fun bindToService(context: Context, callback: ServiceConnection): ServiceToken? {
        var realActivity: Activity? = (context as Activity).parent
        if (realActivity == null)
            realActivity = context


        val contextWrapper = ContextWrapper(realActivity)
        contextWrapper.startService(Intent(contextWrapper, MusicService::class.java))

        val binder = ServiceBinder(callback)
        //绑定服务
        if (contextWrapper.bindService(Intent().setClass(contextWrapper, MusicService::class.java), binder, Context.BIND_AUTO_CREATE)) {
            connectionMap[contextWrapper] = binder
            Timber.v("前台服务连接1,app isn't on foreground")
            return ServiceToken(contextWrapper)
        }
        return null
    }

    class ServiceToken(var wrapperContext: ContextWrapper)

    /**
     * 服务连接
     */
    class ServiceBinder(private val mCallback: ServiceConnection?) : ServiceConnection {
        //服务连接诶时
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder

            MusicServiceRemote.service = binder.service
            mCallback?.onServiceConnected(name, service)
            Timber.tag(TAG).v("前台服务连接2,app isn't on foreground")
        }

        //服务断开时
        override fun onServiceDisconnected(name: ComponentName?) {
            mCallback?.onServiceDisconnected(name)
            service = null
        }
    }

    /**
     * 设置播放列队
     */
    @JvmStatic
    fun setPlayQueue(newQueryList: List<SongLists>, intent: Intent) {
        Timber.d("后台播放2")
        service?.setPlayQueue(newQueryList, intent)

    }

    /**
     *  是否播放
     */
    @JvmStatic
    fun isPlaying():Boolean{
        Timber.v("isPlay是否播放: %s", "isPlaying: ${service?.isPlaying}  ")
        return service?.isPlaying ?:false
    }

    @JvmStatic
    fun getCurrentSong(): SongLists {
        return service?.currentSong ?: SongLists.SONG_LIST
    }

}

