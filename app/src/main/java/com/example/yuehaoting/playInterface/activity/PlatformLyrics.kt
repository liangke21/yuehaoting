package com.example.yuehaoting.playInterface.activity

import com.example.yuehaoting.App
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.lyrics.LyricsReader
import com.example.yuehaoting.util.MusicConstant.KU_GOU
import com.example.yuehaoting.util.MusicConstant.NEW_SONG_KU_GOU
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
 * 作者: LiangKe
 * 时间: 2021/9/28 17:08
 * 描述:
 */
object PlatformLyrics {


    /**
     * 根据不同平台个歌曲适配酷狗歌词
     * @param currentSong SongLists
     */
    suspend fun lyrics(currentSong: SongLists):LyricsReader {
        val mLyricsReader = LyricsReader()
        var file: File?=null
        when (currentSong.platform) {

            NEW_SONG_KU_GOU , KU_GOU-> {

                file = File(App.context.externalCacheDir.toString() + File.separator + "lyrics" + File.separator + "${currentSong.FileHash}.lrcwy")

                if (file.canRead()){
                    Timber.v( "本地歌词纯在%s",file)
                    mLyricsReader.loadLrc(file)
                }else{
                    Timber.v( "本地歌词不在","网络获取歌词")

                  val  l= SongNetwork.songUriID(currentSong.FileHash, "")
                 val id=  l.data.album_id

                    val index= SongNetwork.songUriID(currentSong.FileHash, id)

                    val lyrics= index.data.lyrics

                    mLyricsReader.readLrcText(lyrics,file)
                }


            }


        }

        return mLyricsReader
    }

}