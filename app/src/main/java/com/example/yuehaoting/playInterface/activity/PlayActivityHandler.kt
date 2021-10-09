package com.example.yuehaoting.playInterface.activity

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.yuehaoting.databinding.PlayActivityBinding
import com.example.yuehaoting.main.BottomSheetBehaviorAndBottomNavigationViewMainActivity
import com.example.yuehaoting.util.MusicConstant.UPDATE_TIME_ALL

import com.example.yuehaoting.util.MusicConstant.UPDATE_TIME_ONLY


/**
 * 作者: LiangKe
 * 时间: 2021/9/21 17:24
 * 描述:
 */
class PlayActivityHandler( val activity: ActivityHandlerCallback) : Handler(Looper.getMainLooper()) {

    private lateinit var mActivityHandlerCallback: ActivityHandlerCallback

    override fun handleMessage(msg: Message) {
        mActivityHandlerCallback = activity
        when (msg.what) {
           UPDATE_TIME_ONLY -> {
                mActivityHandlerCallback.onUpdateProgressByHandler()
            }
            UPDATE_TIME_ALL ->{
                mActivityHandlerCallback.onUpdateSeekBarByHandler()
            }
        }
    }


}