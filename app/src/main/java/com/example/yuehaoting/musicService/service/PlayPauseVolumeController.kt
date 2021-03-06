package com.example.yuehaoting.musicService.service

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.annotation.FloatRange
import com.example.yuehaoting.kotlin.launchMain
import timber.log.Timber


/**
 * 作者: 天使
 * 时间: 2021/7/10 9:58
 * 描述:
 */
class PlayPauseVolumeController(private val service: MusicService) {
//<editor-fold desc=" 线程 播放暂停音量控制器" >
    private val handler: Handler = Handler(Looper.getMainLooper())

    companion object {
        private const val DURATION_IN_MS = 600L
    }

    private val fadeInRunnable: Runnable = Runnable {
        val mediaPlayer = service.mediaPlayer

        object : CountDownTimer(DURATION_IN_MS, DURATION_IN_MS / 10) {
            override fun onTick(millisUntilFinished: Long) {
                val volume = 1f - millisUntilFinished * 1.0f / DURATION_IN_MS

                try {
                    mediaPlayer.setVolume(volume, volume)
                } catch (e: IllegalAccessException) {
                    Timber.v("非法访问异常IllegalAccessException:%s", e)
                }
            }

            override fun onFinish() {

                directTo(1f)
            }
        }.start()

    }

    private val fadeOutRunnable: Runnable = Runnable {
        val mediaPlayer = service.mediaPlayer
        object : CountDownTimer(DURATION_IN_MS, DURATION_IN_MS / 10) {
            override fun onTick(millisUntilFinished: Long) {
                val volume = millisUntilFinished * 1.0f / DURATION_IN_MS
                try {
                    mediaPlayer.setVolume(volume, volume)
                } catch (e: IllegalAccessException) {
                    Timber.v("非法访问异常IllegalAccessException:%s", e)
                }
            }

            override fun onFinish() {
                directTo(0f)
                try {
                    mediaPlayer.pause()
                } catch (e: IllegalAccessException) {
                    Timber.v("非法访问异常IllegalAccessException:%s", e)
                }
            }
        }.start()
    }


    fun directTo(@FloatRange(from = 0.0, to = 1.0) toVolume: Float) {
        directTo(toVolume, toVolume)
    }


    private fun directTo(@FloatRange(from = 0.0, to = 1.0) leftVolume: Float, @FloatRange(from = 0.0, to = 1.0) rightVolume: Float) {
        val mediaPlayer = service.mediaPlayer

        try {
            mediaPlayer.setVolume(leftVolume, rightVolume)
        } catch (e: IllegalAccessException) {
            Timber.v("非法访问异常IllegalAccessException:%s", e)
        }
    }

    /**
     * 淡入
     */
    fun fadeIn() {
        handler.removeCallbacks(fadeInRunnable)
        handler.removeCallbacks(fadeOutRunnable)
        handler.post(fadeInRunnable)
    }


    /**
     * 淡出
     */
    fun fadeOut() {
        handler.removeCallbacks(fadeInRunnable)
        handler.removeCallbacks(fadeOutRunnable)
        handler.post(fadeOutRunnable)
    }
    //</editor-fold>

  //<editor-fold desc="协程音量控制器 " >

    /**
     * 淡入 协程
     */
    private fun fadeInRunnable() {
        val mediaPlayer = service.mediaPlayer

        object : CountDownTimer(DURATION_IN_MS, DURATION_IN_MS / 10) {
            override fun onTick(millisUntilFinished: Long) {
                val volume = 1f - millisUntilFinished * 1.0f / DURATION_IN_MS

                try {
                    mediaPlayer.setVolume(volume, volume)
                } catch (e: IllegalAccessException) {
                    Timber.v("非法访问异常IllegalAccessException:%s", e)
                }
            }

            override fun onFinish() {

                directTo(1f)
            }
        }.start()
    }

    /**
     * 淡出协程
     */
    private fun fadeOutRunnable(){
        val mediaPlayer = service.mediaPlayer
        object : CountDownTimer(DURATION_IN_MS, DURATION_IN_MS / 10) {
            override fun onTick(millisUntilFinished: Long) {
                val volume = millisUntilFinished * 1.0f / DURATION_IN_MS
                try {
                    mediaPlayer.setVolume(volume, volume)
                } catch (e: IllegalAccessException) {
                    Timber.v("非法访问异常IllegalAccessException:%s", e)
                }
            }

            override fun onFinish() {
                directTo(0f)
                try {
                    mediaPlayer.pause()
                } catch (e: IllegalAccessException) {
                    Timber.v("非法访问异常IllegalAccessException:%s", e)
                }
            }
        }.start()
    }
    /**
     * 淡入
     */
    fun fadeInCoroutine() {

            fadeInRunnable()


    }


    /**
     * 淡出
     */
    fun fadeOutCoroutine() {

            fadeOutRunnable()


    }
}