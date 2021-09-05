package com.example.yuehaoting

import android.util.MalformedJsonException
import com.example.musiccrawler.hifini.DataSearch
import com.example.yuehaoting.base.DataUri.kuGouSpecialRecommend
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.base.retrofit.SongNetwork.kuGouSpecialRecommend
import com.example.yuehaoting.data.kugou.specialRecommend.SetSpecialRecommend
import com.example.yuehaoting.kotlin.launchMy
import com.example.yuehaoting.kotlin.tryNull
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber

/**
 * 作者: 天使
 * 时间: 2021/8/10 9:32
 * 描述:
 */
object Test {

    fun test() {
/*launchMy {
    val a= SongNetwork.songList("偏爱", "name", "netease", 1)
    Timber.v("运行了吗:%s",a.toString() )
}*/

      /*  launchMy {
            val a = SongNetwork.qqSongList(1, 10, "偏爱")
            Timber.v("运行了吗:%s", a.data?.keyword.toString())
        }*/


   /*     launchMy {
            val b=SongNetwork.kuWoList(1,10,"偏爱")
            Timber.v("运行了吗:%s", b)
        }*/
/*

      launchMy {
          val json="{\n" +
                  "    \"apiver\": 3,\n" +
                  "    \"appid\": 1005,\n" +
                  "    \"client_playlist\": [],\n" +
                  "    \"client_playlist_flag\": 0,\n" +
                  "    \"clienttime\": 1629988069,\n" +
                  "    \"clientver\": 10589,\n" +
                  "    \"key\": \"4a9c47b9ed3868d0cdef6794798e20d3\",\n" +
                  "    \"mid\": \"95751731536823398275799557653797333100\",\n" +
                  "    \"module_id\": 5,\n" +
                  "    \"platform\": \"android\",\n" +
                  "    \"req_multi\": 1,\n" +
                  "    \"session\": \"\",\n" +
                  "    \"special_list\": [\n" +
                  "        {\n" +
                  "            \"A\": 1,\n" +
                  "            \"F\": 1,\n" +
                  "            \"ID\": 0,\n" +
                  "            \"T\": 0\n" +
                  "        }\n" +
                  "    ],\n" +
                  "    \"theme_last_showtime\": 0,\n" +
                  "    \"userid\": \"0\"\n" +
                  "}"

          val gson = Gson()

          val typeOf = object : TypeToken<SetSpecialRecommend>() {}.type

          val appList = gson.fromJson<SetSpecialRecommend>(json, typeOf)

          Timber.e("appList:%s",appList)
      Timber.v("SpecialRecommend数据:%s",kuGouSpecialRecommend(appList))
      }
*/

/*        launchMy {


   try {
       val ss=SongNetwork.kuGouNewSongSongNetwork()

       Timber.v("sssss%s",ss)
   }catch (e:Exception){
       e.message
   }


        }*/

    }
}