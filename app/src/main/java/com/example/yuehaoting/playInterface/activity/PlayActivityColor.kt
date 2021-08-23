package com.example.yuehaoting.playInterface.activity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.widget.SeekBar
import androidx.palette.graphics.Palette
import com.example.yuehaoting.R
import com.example.yuehaoting.databinding.PlayActivityBinding
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.theme.*
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.SetPixelUtil
import com.example.yuehaoting.util.Tag.playColor
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/7/8 16:55
 * 描述:
 */
class PlayActivityColor(private val binding: PlayActivityBinding,private val activity:Activity) {

    var valueAnimator: ValueAnimator? = null

    /**
     * 根据主题修改颜色
     */
    fun setThemeColor() {
        val accentColor = ThemeStore.accentColor
        val tintColor = ThemeStore.playerBtnColor

        //修改控制按钮颜色
        Theme.tintDrawable(binding.layoutPlayLayout.ibPlayNextTrack, R.drawable.play_btn_next, accentColor)
        Theme.tintDrawable(binding.layoutPlayLayout.ibPlayPreviousSong, R.drawable.play_btn_pre, accentColor)
        binding.layoutPlayLayout.ppvPlayPause.setBackgroundColor(65656)
        //进度条颜色
        updateSeeKBarColor(accentColor)

        //歌曲名颜色
        binding.layoutPlayLayoutBar.tvPlaySongName.setTextColor(-1)
        binding.layoutPlayLayoutBar.tvPlaySingerName.setTextColor(-1)


        //修改顶部部件按钮颜色
        Theme.tintDrawable(binding.layoutPlayLayoutBar.ibPlayDropDown, R.drawable.play_drop_down, -1)
        Theme.tintDrawable(binding.layoutPlayLayoutBar.ibPlayNavigation, R.drawable.player_more, -1)

        //播放模式
        val playMode = getSp(activity, MusicConstant.NAME) {
            getInt(MusicConstant.PLAY_MODEL, MusicConstant.LIST_LOOP)
        }
        Theme.tintDrawable(
            binding.layoutPlayLayout.ibPlayPlayMode,
            if (playMode == MusicConstant.LIST_LOOP) R.drawable.play_btn_loop else if (playMode == MusicConstant.RANDOM_PATTERN) R.drawable.play_btn_shuffle else R.drawable.play_btn_loop_one,
            tintColor
        )

        //播放列队
        Theme.tintDrawable(binding.layoutPlayLayout.ibMusicList, R.drawable.play_btn_normal_list, tintColor)
    }

    //进度条颜色
    private fun updateSeeKBarColor(color: Int) {
        setProgressDrawable(binding.sbPlay, color)
        val inset = SetPixelUtil.dip2px(activity, 6f)
        val width = SetPixelUtil.dip2px(activity, 2f)
        val height = SetPixelUtil.dip2px(activity, 6f)
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
     * 图标跟随背图片颜色变化
     */
     fun updateViewsColor(swatch: Palette.Swatch,boolean: Boolean){
        Timber.tag(playColor).v("歌手写真图片颜色:%s",swatch.rgb.toString() +"||"+ Color.parseColor("#FFFFFFFF").toString())
        //播放控件
        binding.layoutPlayLayout.apply {
            //  ibPlayPreviousSong.setColorFilter(swatch.rgb,PorterDuff.Mode.SRC)
            Theme.tintDrawable(ibPlayNextTrack, R.drawable.play_btn_next, -1)
            Theme.tintDrawable(ibPlayPreviousSong,R.drawable.play_btn_pre, -1)
            ppvPlayPause.setBackgroundColor(swatch.rgb)
        }

        updateSeeKBarColor(ColorUtil.adjustAlpha(swatch.rgb, 0.5f))
        //播放模式
        val playMode = getSp(activity, MusicConstant.NAME) {
            getInt(MusicConstant.PLAY_MODEL, MusicConstant.LIST_LOOP)
        }
        Theme.tintDrawable(
            binding.layoutPlayLayout.ibPlayPlayMode,
            if (playMode == MusicConstant.LIST_LOOP) R.drawable.play_btn_loop else if (playMode == MusicConstant.RANDOM_PATTERN) R.drawable.play_btn_shuffle else R.drawable.play_btn_loop_one,
            -1
        )

        //播放列队
        Theme.tintDrawable(binding.layoutPlayLayout.ibMusicList, R.drawable.play_btn_normal_list, -1)
        //是否开启背景图片为颜色
          if (boolean){
              Timber.tag(playColor).v("是否开启背景图片为颜色歌手写真图片颜色:%s",boolean)
              startBGColorAnimation(swatch)
          }

    }

    /**
     * 背景根据图片变化
     */
    @SuppressLint("Recycle")
    private fun startBGColorAnimation(swatch: Palette.Swatch) {

        valueAnimator?.cancel()

        valueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), Theme.resolveColor(activity, R.attr.colorSurface), swatch.rgb)

        valueAnimator?.addUpdateListener { animation ->
            val drawable = DrawableGradient(GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(animation.animatedValue as Int,
                    Theme.resolveColor(activity, R.attr.colorSurface)), 0)
            binding.playerContainer.background = drawable
            Timber.tag(playColor).v("显示颜色背景:%s")
        }
        //这里重点注意,如果设置为1000毫秒,动画会在后台执行,无法代替直到结束
        valueAnimator?.setDuration(0)?.start()
    }






}