package com.example.yuehaoting.playInterface.livedata

import android.content.Intent
import androidx.lifecycle.liveData
import com.example.yuehaoting.base.retrofit.SongNetwork.singerPhoto
import com.example.yuehaoting.data.kugou.RecordData
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhoto
import com.example.yuehaoting.util.MusicConstant.SINGER_ID
import com.example.yuehaoting.util.MusicConstant.SINGER_PHOTO
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * 作者: 天使
 * 时间: 2021/6/27 15:34
 * 描述:  酷狗音乐真
 */
object KuGouSongPhoto {

  fun setSingerPhoto(MixSongID: String)= liveData(Dispatchers.IO) {
      val result = try {
          run {
              Timber.v("歌手写真ID : %S", MixSongID)
              val singerPhoto = singerPhoto("[{\"album_audio_id\":$MixSongID}]")
              val phoneSingerPhoto = singerPhoto.data[0][0].imgs.`4`

              phoneSingerPhoto.forEach {
                  Timber.v("歌手写真url: %s" ,it)
              }
              Result.success(phoneSingerPhoto)
          }

      }catch (e: Exception) {
          Result.failure<RecordData>(e)
      }
       emit(result)
    }

}