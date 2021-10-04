package com.example.yuehaoting.playInterface.activity

import android.annotation.SuppressLint
import android.view.View
import com.example.yuehaoting.R
import com.example.yuehaoting.base.db.DatabaseRepository.Companion.getInstance
import com.example.yuehaoting.base.recyclerView.baseAdapter.BaseAdapter
import com.example.yuehaoting.base.recyclerView.baseAdapter.BaseViewHolder
import com.example.yuehaoting.base.rxJava.RxUtil
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.PlayDialogFragmentItemBinding
import com.example.yuehaoting.musicService.service.MusicServiceRemote.getCurrentSong
import com.example.yuehaoting.musicService.service.MusicServiceRemote.getDuration
import com.example.yuehaoting.musicService.service.MusicServiceRemote.getProgress
import com.example.yuehaoting.theme.ThemeStore
import com.example.yuehaoting.theme.ThemeStore.textColorPrimary
import com.example.yuehaoting.util.BroadcastUtil.sendLocalBroadcast
import com.example.yuehaoting.util.IntentUtil.makeCodIntent
import com.example.yuehaoting.util.MusicConstant.NEXT
import com.example.yuehaoting.playInterface.activity.PlayActivityDialogFragmentAdapter.PlayQueueHolder
import com.example.yuehaoting.util.Tag
import timber.log.Timber

/**
 * Created by Remix on 2015/12/2.
 */
/**
 * 正在播放列表的适配器
 */
class PlayActivityDialogFragmentAdapter(layoutId: Int) : BaseAdapter<SongLists, PlayQueueHolder>(layoutId) {
    private val accentColor: Int = ThemeStore.accentColor
    private val textColor: Int = textColorPrimary

    @SuppressLint("SetTextI18n")
    override fun convert(holder: PlayQueueHolder, data: SongLists?, position: Int) {
        if (data == null) {
            return
        }
        if (data == SongLists.SONG_LIST) {
            //歌曲已经失效
            holder.binding.tvPlayDialogFragmentRecyclerViewA.text = R.string.song_lose_effect.toString()
            holder.binding.tvPlayDialogFragmentRecyclerViewB.visibility = View.GONE
            return
        }
        //设置歌曲与艺术家
        holder.binding.tvPlayDialogFragmentRecyclerViewA.text = data.SongName
        holder.binding.tvPlayDialogFragmentRecyclerViewB.text = " - ${data.SingerName}"

        Timber.tag(Tag.queueDatabase).v("当前歌曲id %s 本地数据库id %s",getCurrentSong().id, data.id)
        //高亮
        if (getCurrentSong().id == data.id) {
            // holder.binding.tvPlayDialogFragmentRecyclerViewB.setTextColor(accentColor)
            holder.binding.tvPlayDialogFragmentRecyclerViewA.setTextColor(textColor)
            holder.binding.lottie.visibility = View.VISIBLE
            // holder.binding.lottie.loop(true)
            holder.binding.lottie.repeatCount = getDuration() - getProgress()
            holder.binding.lottie.playAnimation()
        } else {
//                holder.mSong.setTextColor(Color.parseColor(ThemeStore.isDay() ? "#323335" : "#ffffff"));
            holder.binding.tvPlayDialogFragmentRecyclerViewA.setTextColor(textColor)
            holder.binding.lottie.visibility = View.GONE
            //holder.binding.lottie.pauseAnimation()
        }
        //删除按钮
        holder.binding.ivPlayDialogFragmentRecyclerView.setOnClickListener {
            getInstance()
                .deleteFromPlayQueue(listOf(data.id))
                .compose(RxUtil.applySingleScheduler())
               .subscribe { num: Int ->
                    //删除的是当前播放的歌曲
                    if (num > 0 && getCurrentSong().id == data.id) {
                        sendLocalBroadcast(makeCodIntent(NEXT))  //发送一条广播给后台播放下一首
                    }
                }

        }
        holder.binding.itemRoot.setOnClickListener { v: View? ->
            onItemClickListener?.onItemClick(v, holder.adapterPosition)
        }
    }

    class PlayQueueHolder(view: View) : BaseViewHolder(view) {
        val binding: PlayDialogFragmentItemBinding = PlayDialogFragmentItemBinding.bind(view)
    }
}