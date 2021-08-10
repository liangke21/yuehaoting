package com.example.yuehaoting

import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.kotlin.launchMy
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


        launchMy {
            val b=SongNetwork.kuWoList(1,10,"偏爱")
            Timber.v("运行了吗:%s", b)
        }

    }
}