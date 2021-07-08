package com.example.yuehaoting.playInterface.activity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.*
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Switch
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.App
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.PlayBaseActivity
import com.example.yuehaoting.base.kapt.YourAppGlideModule
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhoto
import com.example.yuehaoting.databinding.PlayActivityBinding
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.kotlin.lazyMy
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.util.BroadcastUtil
import timber.log.Timber
import com.example.yuehaoting.musicService.service.MusicServiceRemote.isPlaying
import com.example.yuehaoting.playInterface.activity.SingerPhoto.handler
import com.example.yuehaoting.playInterface.activity.SingerPhoto.handlerRemoveCallbacks
import com.example.yuehaoting.playInterface.activity.SingerPhoto.photoCycle
import com.example.yuehaoting.playInterface.activity.SingerPhoto.singerPhotoUrl
import com.example.yuehaoting.playInterface.viewmodel.PlayViewModel
import com.example.yuehaoting.theme.*

import com.example.yuehaoting.util.MusicConstant.BACKGROUND_ADAPTIVE_COLOR
import com.example.yuehaoting.util.MusicConstant.NAME
import com.example.yuehaoting.util.MusicConstant.NEXT
import com.example.yuehaoting.util.MusicConstant.PAUSE_PLAYBACK
import com.example.yuehaoting.util.MusicConstant.PLAYER_BACKGROUND
import com.example.yuehaoting.util.MusicConstant.PREV
import com.example.yuehaoting.util.MusicConstant.SINGER_ID
import com.example.yuehaoting.util.MusicConstant.BACKGROUND_CUSTOM_IMAGE
import com.example.yuehaoting.util.MusicConstant.LIST_LOOP
import com.example.yuehaoting.util.MusicConstant.PLAY_MODEL
import com.example.yuehaoting.util.MusicConstant.RANDOM_PATTERN
import com.example.yuehaoting.util.MusicConstant.SINGER_NAME
import com.example.yuehaoting.util.MusicConstant.SONG_NAME
import com.example.yuehaoting.util.SetPixelUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Callable
import kotlin.collections.ArrayList


class PlayActivity : PlayBaseActivity() {
    private lateinit var binding: PlayActivityBinding
    private val myUtil = BroadcastUtil()
    private val viewModel by lazyMy { ViewModelProviders.of(this).get(PlayViewModel::class.java) }
    private val mCacheUrl = CacheUrl()

    private var valueAnimator: ValueAnimator? = null
    /**
     * 当前是否播放
     */
    private var isPlaying = false

    /**
     * 背景
     */
    private val background by lazyMy {
        getSp(this, NAME) {
            getInt(PLAYER_BACKGROUND, BACKGROUND_ADAPTIVE_COLOR)
        }
    }

    override fun setSatuBarColor() {
        when (background) {
            // 背景自适应  更是封面
            BACKGROUND_ADAPTIVE_COLOR -> {
                StatusBarUtil.setTransparent(this)
                Timber.v("播放界面状态栏背景 背景自适应  更是封面 : %s", background)
            }
            //背景图片定义
            BACKGROUND_CUSTOM_IMAGE -> {
                StatusBarUtil.setTransparent(this)

                val futureTarget = Glide.with(this)
                    .load(viewModel.singerPhotoList[10].sizable_portrait)
                    .submit() as FutureTarget<Bitmap>
                val bitmap: Bitmap = futureTarget.get()
                Timber.v("歌手写真单个uri:%s", viewModel.singerPhotoList[10].sizable_portrait)
                binding.playerContainer.background = BitmapDrawable(resources, bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlayActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mCacheUrl.init(this)

        receiveIntent()
        observeSingerPhotoData()
        setThemeColor()
        initView()
    }

    //初始化控件
    private fun initView() {
        arrayOf(
            binding.layoutPlayLayout.ibPlayPreviousSong,
            binding.layoutPlayLayout.flPlayContainer,
            binding.layoutPlayLayout.ibPlayNextTrack
        ).forEach {
            it.setOnClickListener(onCtrlClick)
        }

        //标题栏设置 歌手歌词
        binding.layoutPlayLayoutBar.apply {
            val songName = intent.getStringExtra(SONG_NAME)
            tvPlaySongName.text = songName
            val singerName = intent.getStringExtra(SINGER_NAME)
            tvPlaySingerName.text = singerName
        }
    }

    //接收数据
    private fun receiveIntent() {
        val singerId = intent.getStringExtra(SINGER_ID)

        val list = mCacheUrl.getFromDisk(singerId.toString())
        if (list != null) {
            photoCycle(list, binding.playerContainer,resources,::updateUi)
        } else {
            Timber.v("歌手id: %S", singerId)

            Glide.with(this).asBitmap()
                .load(R.drawable.youjing)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        binding.playerContainer.background = BitmapDrawable(resources, resource)
                    }
                })

            if (singerId != null) {
                viewModel.singerId(singerId)
            }
        }
    }

    private fun observeSingerPhotoData() {


        tryNull {
            viewModel.singerIdObservedData.observe(this) {
                //获取图片连接
                val urlList = singerPhotoUrl(it)
                val singerId = intent.getStringExtra(SINGER_ID)
                mCacheUrl.putToDisk(singerId.toString(), urlList)
                //把图片设置为背景
                photoCycle(urlList, binding.playerContainer,resources,::updateUi)
            }
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
    private fun setThemeColor() {
        val accentColor = ThemeStore.accentColor
        val tintColor = ThemeStore.playerBtnColor

        //修改控制按钮颜色
        Theme.tintDrawable(binding.layoutPlayLayout.ibPlayNextTrack, R.drawable.play_btn_next, accentColor)
        Theme.tintDrawable(binding.layoutPlayLayout.ibPlayPreviousSong, R.drawable.play_btn_pre, accentColor)
        binding.layoutPlayLayout.ppvPlayPause.setBackgroundColor(accentColor)
        //进度条颜色
        updateSeeKBarColor(accentColor)

        //歌曲名颜色
        binding.layoutPlayLayoutBar.tvPlaySongName.setTextColor(ThemeStore.playerBtnColor)

        //修改顶部部件按钮颜色
        Theme.tintDrawable(binding.layoutPlayLayoutBar.ibPlayDropDown, R.drawable.play_drop_down, tintColor)
        Theme.tintDrawable(binding.layoutPlayLayoutBar.ibPlayNavigation, R.drawable.player_more, tintColor)

        //播放模式
        val playMode = getSp(this, NAME) {
            getInt(PLAY_MODEL, LIST_LOOP)
        }
        Theme.tintDrawable(
            binding.layoutPlayLayout.ibPlayPlayMode,
            if (playMode == LIST_LOOP) R.drawable.play_btn_loop else if (playMode == RANDOM_PATTERN) R.drawable.play_btn_shuffle else R.drawable.play_btn_loop_one,
            tintColor
        )

        //播放列队
        Theme.tintDrawable(binding.layoutPlayLayout.ibMusicList, R.drawable.play_btn_normal_list, tintColor)
    }

    //进度条颜色
    private fun updateSeeKBarColor(color: Int) {
        setProgressDrawable(binding.sbPlay, color)
        val inset = SetPixelUtil.dip2px(this, 6f)
        val width = SetPixelUtil.dip2px(this, 2f)
        val height = SetPixelUtil.dip2px(this, 6f)
        binding.sbPlay.thumb = InsetDrawable(
            GradientDrawableMaker()
                .width(width)
                .height(height)
                .color(color)
                .make(),
            inset, inset, inset, inset
        )
    }

    //绘制进度条
    private fun setProgressDrawable(seekBar: SeekBar, color: Int) {

        val progressDrawable = seekBar.progressDrawable as LayerDrawable
        //修改进度条颜色
        (progressDrawable.getDrawable(0) as GradientDrawable).setColor(ThemeStore.playerBtnColor)
        progressDrawable.getDrawable(1).setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    /**
     * 更新播放暂停按钮
     */
    private fun updatePlayButton(isPlay: Boolean) {
        isPlaying = isPlay
        binding.layoutPlayLayout.ppvPlayPause.updateStRte(isPlay, true)
    }

    override fun onPause() {
        //结束写真幻影灯片
        handlerRemoveCallbacks()
        super.onPause()

    }

  @SuppressLint("CheckResult")
  private fun updateUi(bitmap: Bitmap){

      Single.fromCallable{bitmap}.map{ result ->
          val palette=Palette.from(result).generate()
          if (palette.mutedSwatch!=null){
              return@map palette.mutedSwatch
          }
          val swatches=ArrayList<Palette.Swatch>(palette.swatches)

          swatches.sortWith(Comparator{ O1, O2-> O1.population.compareTo(O2.population) })
          return@map if (swatches.isEmpty()) swatches[0] else Palette.Swatch(Color.GRAY, 100)

      }
          .onErrorReturnItem((Palette.Swatch(Color.GRAY,100)))
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({swatch->

              if (swatch==null){
                  return@subscribe
              }
              updateViewsColor(swatch)
              //startBGColorAnimation(swatch)
          }){t:Throwable?->Timber.v(t)}


  }

  private fun updateViewsColor(swatch: Palette.Swatch){
      binding.layoutPlayLayout.apply {
        //  ibPlayPreviousSong.setColorFilter(swatch.rgb,PorterDuff.Mode.SRC)
          Theme.tintDrawable(ibPlayNextTrack, R.drawable.play_btn_next, swatch.rgb)
          Theme.tintDrawable(ibPlayPreviousSong,R.drawable.play_btn_pre, swatch.rgb)
          ppvPlayPause.setBackgroundColor(swatch.rgb)


      }

  }

/*    private fun startBGColorAnimation(swatch: Palette.Swatch) {
        valueAnimator?.cancel()

        valueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), Theme.resolveColor(this, R.attr.colorSurface), swatch.rgb)

        valueAnimator?.addUpdateListener { animation ->
            val drawable = DrawableGradient(GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(animation.animatedValue as Int,
                    Theme.resolveColor(this, R.attr.colorSurface)), 0)
            binding.playerContainer.background = drawable
        }
        valueAnimator?.setDuration(1000)?.start()
    }*/

/*    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                moveTaskToBack(true);
                return true;
            }
        }

        return super.onKeyUp(keyCode, event)
    }*/

}