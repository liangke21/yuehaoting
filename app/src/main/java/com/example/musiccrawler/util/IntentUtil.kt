package com.example.musiccrawler.util

import android.content.Intent
import com.example.musiccrawler.util.MusicConstant.ACTION_CMD
import com.example.musiccrawler.util.MusicConstant.EXTRA_CONTROL
import com.example.musiccrawler.util.MusicConstant.EXTRA_SHUFFLE

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 14:14
 * 描述:
 */
class IntentUtil {


    private fun makeCodIntent(cmd: Int, shuffle: Boolean): Intent {

        return Intent(ACTION_CMD).putExtra(EXTRA_CONTROL, cmd)
            .putExtra(EXTRA_SHUFFLE,shuffle)
    }

    fun makeCodIntent(cmd:Int):Intent{
        return makeCodIntent(cmd,false)
    }

}