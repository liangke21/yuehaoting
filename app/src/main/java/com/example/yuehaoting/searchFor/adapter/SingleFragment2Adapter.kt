package com.example.yuehaoting.searchFor.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musiccrawler.hifini.DataSearch
import com.example.yuehaoting.R
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.playInterface.activity.PlayActivity
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.HIF_INI
import com.example.yuehaoting.util.MusicConstant.KEY_MUSIC_PLATFORM
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/7/13 16:23
 * 描述:
 */
class SingleFragment2Adapter(private val list: List<DataSearch.Attributes>,private val activity: FragmentActivity?) : RecyclerView.Adapter<SingleFragment2Adapter.ViewHolder>() {
    private val musicUtil = IntentUtil()
    private val songList = ArrayList<SongLists>()
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.tv_search_hifIni_song_title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fragment_hifini2, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songName.text = list[position].songTitle

        val songLists = list[position].songTitle.split("《")
        val singerName = songLists[0]
        val songName = songLists[1].replace("》", "")

        songList.add(
            SongLists(
                songName,
                singerName,
                list[position].songHref,
                "2325",
                HIF_INI
            )
        )

        songPlay(holder,position,songList,"")

    }


    private fun songPlay(holder: ViewHolder, position: Int, songLists: ArrayList<SongLists>, mixSongID: String) {
        holder.itemView.setOnClickListener {

            Timber.d("后台播放1")
            if(MusicServiceRemote.isPlaying() && songLists[position]== MusicServiceRemote.getCurrentSong()){
                val intent= Intent(activity, PlayActivity::class.java)
                intent.putExtra(MusicConstant.CURRENT_SONG,songLists[position])
                activity?.startActivity(intent)
            }else{
                MusicServiceRemote.setPlayQueue(songLists, musicUtil.makeCodIntent(MusicConstant.PLAY_SELECTED_SONG).putExtra(MusicService.EXTRA_POSITION, position))
                val intent= Intent(activity, PlayActivity::class.java)
                intent.putExtra(MusicConstant.SINGER_ID,mixSongID)
                intent.putExtra(MusicConstant.CURRENT_SONG,songLists[position])
                activity?.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}