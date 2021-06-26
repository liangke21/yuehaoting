package com.example.yuehaoting.searchFor.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.musicService.service.Command
import com.example.yuehaoting.musicService.service.MusicService
import com.example.yuehaoting.musicService.service.MusicServiceRemote.setPlayQueue
import com.example.yuehaoting.util.MusicUtil
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.playInterface.activity.PlayActivity

import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/1 10:55
 * 描述:
 */
class SingleFragment1Adapter(private val list: List<KuGouSingle.Data.Lists>, val activity: FragmentActivity?) :
    RecyclerView.Adapter<SingleFragment1Adapter.ViewHolder>() {
    private val musicUtil = MusicUtil()
    private val songLists = ArrayList<SongLists>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.rv_fragment_search_Single_SongName)
        val albumName: TextView = view.findViewById(R.id.rv_fragment_search_Single_AlbumName)
        val hp: ImageView = view.findViewById(R.id.iv_hq)
        val sp: ImageView = view.findViewById(R.id.iv_sq)
        val vip: ImageView = view.findViewById(R.id.iv_vip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fragment_search_single_rv_content, parent, false)
        return ViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val song = songDetails(position)


        holder.songName.text = song[0]
        holder.albumName.text = song[1] + song[2]
        //设置音质图标
        songSoundQuality(holder, position)


        songLists.add(
            SongLists(
                song[0],
                song[1],
                list[position].FileHash
            )
        )
        //播放监听
        songPlay(holder, position, songLists)


    }


    private fun songPlay(holder: ViewHolder, position: Int, songLists: ArrayList<SongLists>) {
        holder.itemView.setOnClickListener {

            Timber.d("后台播放1")


            setPlayQueue(
                songLists,
                musicUtil.makeCodIntent(Command.PLAYSONG)
                    .putExtra(MusicService.EXTRA_POSITION, position)
            )

           val intent=Intent(activity,PlayActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    private fun songSoundQuality(holder: ViewHolder, position: Int) {
        val mList = list[position]
        val hQFileHash = mList.HQFileHash
        if (!TextUtils.isEmpty(hQFileHash)) {
            holder.hp.setImageResource(R.drawable.search_hq)
        }
        val sQFileHash = mList.SQFileHash
        if (!TextUtils.isEmpty(sQFileHash)) {
            holder.sp.setImageResource(R.drawable.search_sq)
        }
    }

    /**
     * @Param position 数据长度
     * @return songName 表示歌曲标题,singerName表示歌曲歌手,albumName 歌曲专辑
     **/
    private fun songDetails(position: Int): Array<String> {
        val mList = list[position]
        //歌曲名字
        var songName = mList.SongName
        if (mList.SongName.contains("<em>")) {
            songName = songName.replace("<em>", "").replace("</em>", "")
            println(songName)
        }
        //歌手
        var singerName = mList.SingerName
        if (singerName.contains("<em>")) {
            singerName = singerName.replace("<em>", "").replace("</em>", "")

        }
        //专辑
        var albumName = mList.AlbumName

        if (albumName.contains("<em>")) {
            albumName = albumName.replace("<em>", "").replace("</em>", "")

        }
        if (!TextUtils.isEmpty(albumName)) {
            albumName = "-《$albumName》"
        }

        return arrayOf(songName, singerName, albumName)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}