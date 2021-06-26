package com.example.yuehaoting.playInterface.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.BaseActivity

import com.example.yuehaoting.databinding.PlayActivityBinding
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.util.Constants
import com.example.yuehaoting.util.MyUtil
import timber.log.Timber

class PlayActivity : BaseActivity() {
    private lateinit var binding: PlayActivityBinding
    private val myUtil=MyUtil()

    /**
     * 当前是否播放
     */
    private var isPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlayActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)



      arrayOf(
            binding.layoutPlayLayout.ibPlayPreviousSong,
            binding.layoutPlayLayout.flPlayContainer,
            binding.layoutPlayLayout.ibPlayNextTrack
        ).forEach {
            it.setOnClickListener(onCtrlClick)
        }


    }

    /**
     * 播放上一首 暂停 下一首
     */
    private val onCtrlClick= View.OnClickListener { v ->
        val intent=Intent(MusicService.ACTION_CMD)
        when(v.id){
            R.id.ib_play_Previous_song->{
                intent.putExtra(MusicService.EXTRA_CONTROL,Constants.PREV)
                Timber.v("播放上一首1: %s",Constants.PREV)
            }
            R.id.fl_play_container->intent.putExtra(MusicService.EXTRA_CONTROL,Constants.PAUSE_PLAYBACK)
            R.id.ib_play_next_track->{
                intent.putExtra(MusicService.EXTRA_CONTROL,Constants.NEXT)
                Timber.v("播放下一首1: %s",Constants.NEXT)
            }
        }

        myUtil.sendLocalBroadcast(intent)
    }

    override fun onPlayStateChange() {
        super.onPlayStateChange()

    }
    /**
     * 更新播放暂停按钮
     */
  private  fun updatePlayButton(isPlay:Boolean){
       isPlaying=isPlay
        binding.layoutPlayLayout.ppvPlayPause.updateStRte(isPlay,true)
    }
    override fun onDestroy() {
        super.onDestroy()

    }
}