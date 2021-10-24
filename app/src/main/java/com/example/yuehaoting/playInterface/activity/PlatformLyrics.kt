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
    suspend fun lyrics(currentSong: SongLists):LyricsReader? {
       when(currentSong.platform){
           2,4,5,6,7 -> return null
       }
        val mLyricsReader = LyricsReader()
        var file: File?=null
        when (currentSong.platform) {

            NEW_SONG_KU_GOU , KU_GOU-> {
               try{
                file = File(App.context.externalCacheDir.toString() + File.separator + "lyrics" + File.separator + "${currentSong.FileHash}.lrcwy")

                if (file.canRead()){

                    Timber.tag("歌词").v( "加载本地歌词纯在%s",file)
                    mLyricsReader.loadLrc(file)
                }else{
                    Timber.tag("歌词").v( "本地歌词不在  %s","获取网络获取歌词")

                  val  l= SongNetwork.songUriID(currentSong.FileHash, "")
                 val id=  l.data.album_id

                    val index= SongNetwork.songUriID(currentSong.FileHash, id)

                    val lyrics= index.data.lyrics
                    val s="[00:00.000] 作词 : 林文炫\n[00:01.000] 作曲 : 胡彦斌\n[00:29.90]打开地狱的 大门\n[00:35.55]\n[00:37.03]不请自来 贪欲念\n[00:42.04]\n[00:44.12]无常路上 买命钱\n[00:49.71]\n[00:51.28]是生是畜 黄泉见\n[00:56.72]\n[00:58.64]还魂门前 许个愿\n[01:03.79]\n[01:04.46]不要相约 来世见\n[01:11.85]\n[01:12.86]盗不到的 叫永远\n[01:18.81]解不开的 是心门\n[01:28.14]\n[01:30.94]最美的是 遗言\n[01:33.76]\n[01:34.42]最丑的是 誓言\n[01:37.39]\n[01:38.02]那些无法 的改变\n[01:40.99]\n[01:41.55]就在放下 举起间\n[01:45.25]最假的是 眼泪\n[01:48.12]\n[01:48.78]最真的看 不见\n[01:52.34]那些无法 的改变\n[01:55.46]就在放下 举起间\n[02:03.24]\n[02:14.82]还魂门前 许个愿\n[02:19.77]\n[02:20.51]不要相约 来世见\n[02:27.82]\n[02:28.93]盗不到的 叫永远\n[02:34.34]\n[02:34.86]解不开的 是心门\n[02:42.64]\n[02:43.34]最美的是 遗言\n[02:46.92]最丑的是 誓言\n[02:49.95]\n[02:50.51]那些无法 的改变\n[02:53.04]\n[02:54.12]就在放下 举起间\n[02:57.76]最假的是 眼泪\n[03:01.22]最真的看 不见\n[03:04.25]\n[03:04.94]那些无法 的改变\n[03:08.03]就在放下 举起间\n[03:16.93]\n[03:22.79]最美的是 遗言\n[03:25.83]\n[03:26.37]最丑的是 誓言\n[03:29.18]\n[03:29.94]那些无法 的改变\n[03:33.40]就在放下 举起间\n[03:37.13]最假的是 眼泪\n[03:40.72]最真的看 不见\n[03:43.69]\n[03:44.32]那些无法 的改变\n[03:47.36]就在放下 举起间\n"
                    mLyricsReader.readLrcText(lyrics,file)
                }
               }catch (e: Exception) {
                   e.printStackTrace()
               }

            }


        }

        return mLyricsReader
    }

}