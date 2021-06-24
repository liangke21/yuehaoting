package com.example.yuehaoting.util

import android.content.Intent
import com.example.yuehaoting.musicPath.service.MusicService

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 14:14
 * 描述:
 */
class MusicUtil {


    private fun makeCodIntent(cmd: Int, shuffle: Boolean): Intent {

        return Intent(MusicService.ACTION_CMD).putExtra(MusicService.EXTRA_CONTROL, cmd)
            .putExtra(MusicService.EXTRA_SHUFFLE,shuffle)
    }

    fun makeCodIntent(cmd:Int):Intent{
        return makeCodIntent(cmd,false)
    }

}