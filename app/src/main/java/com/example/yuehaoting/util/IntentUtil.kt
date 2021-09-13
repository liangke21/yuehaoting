package com.example.yuehaoting.util

import android.content.Intent
import com.example.yuehaoting.util.MusicConstant.ACTION_CMD
import com.example.yuehaoting.util.MusicConstant.EXTRA_CONTROL
import com.example.yuehaoting.util.MusicConstant.EXTRA_SHUFFLE

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 14:14
 * 描述:
 */
class IntentUtil {


    private fun makeCodIntent(cmd: Int, shuffle: Boolean): Intent {

        return Intent(ACTION_CMD).putExtra(EXTRA_CONTROL, cmd)    //顺序播放
            .putExtra(EXTRA_SHUFFLE,shuffle)  //随机播放
    }

    /**
     * 后台控制播放
     * @param cmd Int  当前选中歌曲
     * @return Intent
     */
    fun makeCodIntent(cmd:Int):Intent{
        return makeCodIntent(cmd,false)
    }

}