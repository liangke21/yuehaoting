package com.example.yuehaoting.playInterface.livedata

import androidx.lifecycle.liveData
import com.example.yuehaoting.base.retrofit.SongNetwork.singerPhoto
import com.example.yuehaoting.data.kugouSingerPhoto.SingerPhotoData

import kotlinx.coroutines.Dispatchers
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/6/27 15:34
 * 描述:  酷狗音乐真
 */
object KuGouSongPhoto {

  fun setSingerPhoto(MixSongID: String)= liveData(Dispatchers.IO) {
      val result :Result<SingerPhotoData> = try {
          val singerPhoto = singerPhoto("[{\"album_audio_id\":$MixSongID}]")
          run {
              Timber.v("歌手写真ID : %S", MixSongID)
          //  val  phoneSingerPhoto = singerPhoto.data[0][0].imgs.`4`

  /*            phoneSingerPhoto.forEach {
                  Timber.v("歌手写真url: %s" ,it)
              }*/
             Result.success(singerPhoto)
          }

      }catch (e:Exception){
          e.printStackTrace()
          Result.failure(e)
      }
       emit(result)
    }

}