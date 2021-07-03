package com.example.yuehaoting.playInterface.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.putInt
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.PlayBaseActivity
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhoto
import com.example.yuehaoting.databinding.PlayActivityBinding
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.util.Constants
import com.example.yuehaoting.util.MyUtil
import timber.log.Timber
import com.example.yuehaoting.musicService.service.MusicServiceRemote.isPlaying
import com.example.yuehaoting.kotlin.setSp
import com.example.yuehaoting.playInterface.livedata.KuGouSongPhoto
import com.example.yuehaoting.playInterface.viewmodel.PlayViewModel
import com.example.yuehaoting.statusBar.StatusBarUtil

import com.example.yuehaoting.util.MusicConstant.BACKGROUND_ADAPTIVE_COLOR
import com.example.yuehaoting.util.MusicConstant.NAME
import com.example.yuehaoting.util.MusicConstant.NEXT
import com.example.yuehaoting.util.MusicConstant.PAUSE_PLAYBACK
import com.example.yuehaoting.util.MusicConstant.PLAYER_BACKGROUND
import com.example.yuehaoting.util.MusicConstant.PREV
import com.example.yuehaoting.util.MusicConstant.SINGER_ID


class PlayActivity : PlayBaseActivity() {
    private lateinit var binding: PlayActivityBinding
    private val myUtil = MyUtil()
private val viewModel by lazyMy { ViewModelProviders.of(this).get(PlayViewModel::class.java) }
    /**
     * 当前是否播放
     */
    private var isPlaying = false

    /**
     * 背景
     */
    private val background by lazyMy {
     getSp(this , NAME){
        getInt(PLAYER_BACKGROUND, BACKGROUND_ADAPTIVE_COLOR)
        }
    }

    override fun setSatuBarColor() {
       when(background){
           // 背景自适应  更是封面
           BACKGROUND_ADAPTIVE_COLOR->{
             StatusBarUtil.setTransparent(this)
               Timber.v("播放界面状态栏背景 背景自适应  更是封面 : %s",background)
           }
       }
    }

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
        receiveIntent()
        observeSingerPhotoData()
    }
    //接收数据
    private fun receiveIntent() {
        val singerId=   intent.getStringExtra(SINGER_ID)
        Timber.v("歌手id: %S",singerId)
        if (singerId != null) {
            viewModel.singerId(singerId)
        }
    }
 private fun observeSingerPhotoData(){
     viewModel.singerIdObservedData.observe(this) {
         val singerPhotoUir = it.getOrNull() as ArrayList<SingerPhoto.Data.Imgs.Data4>


         Timber.v("歌手写真连接: %s", singerPhotoUir)
     }
 }
    /**
     * 播放上一首 暂停 下一首
     */
    private val onCtrlClick = View.OnClickListener { v ->
        val intent = Intent(MusicService.ACTION_CMD)
        when (v.id) {
            R.id.ib_play_Previous_song -> {
                intent.putExtra(MusicService.EXTRA_CONTROL, PREV)
                Timber.v("播放上一首1: %s", PREV)
            }
            R.id.fl_play_container -> intent.putExtra(MusicService.EXTRA_CONTROL, PAUSE_PLAYBACK)
            R.id.ib_play_next_track -> {
                intent.putExtra(MusicService.EXTRA_CONTROL, NEXT)
                Timber.v("播放下一首1: %s", NEXT)
            }
        }

        myUtil.sendLocalBroadcast(intent)
    }

    override fun onServiceConnected(musicService: MusicService) {
        super.onServiceConnected(musicService)
        onPlayStateChange()
    }

    //播放状态已更改
    override fun onPlayStateChange() {
        super.onPlayStateChange()
        //更新按钮状态
        val isPlay = isPlaying()
        if (isPlay != isPlay) {
            updatePlayButton(isPlay)
        }

    }
    /**
     * 根据主题修改颜色
     */


    /**
     * 更新播放暂停按钮
     */
    private fun updatePlayButton(isPlay: Boolean) {
        isPlaying = isPlay
        binding.layoutPlayLayout.ppvPlayPause.updateStRte(isPlay, true)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}