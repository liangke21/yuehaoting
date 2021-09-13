package com.example.yuehaoting.util

import com.example.yuehaoting.data.kugou.NewSong

/**
 * 作者: 天使
 * 时间: 2021/6/27 13:57
 * 描述: 常量
 */
object MusicConstant {
    //_______________________________________|Sp key|______________________________________________________________________________________________________
//包名
    private const val PACKAGE_NAME = "com.example.yuehaoting"
    const val NAME = "Setting"  //xml 文件
    const val PLAY_MODEL = "play_model"

    //自定义播放背景
    const val PLAYER_BACKGROUND = "player_background"

    //_______________________________________|播放模式命令|______________________________________________________________________________________________________
    const val LIST_LOOP = 1
    const val RANDOM_PATTERN = 2
    const val SINGLE_CYCLE = 3

    //_______________________________________|播放界面背景|______________________________________________________________________________________________________
    //背景自适应颜色
    const val BACKGROUND_ADAPTIVE_COLOR = 1

    //背景自定义图片
    const val BACKGROUND_CUSTOM_IMAGE = 2

    //_______________________________________|播放控制|______________________________________________________________________________________________________
    /**
     * 当前选定歌曲
     */
    const val PLAY_SELECTED_SONG = 0

    //上一首
    const val PREV = 1

    //暂停播放
    const val PAUSE_PLAYBACK = 2

    //下一首
    const val NEXT = 3

    /**
     * 控制歌曲 播放
     * 如 播放选定歌曲 上一首 下一首
     */
    const val EXTRA_CONTROL = "Control"

    //随机播放
    const val EXTRA_SHUFFLE = "shuffle"

    /**
     * 当前播放歌曲
     */
    const val CURRENT_SONG ="CurrentSong"

    /**
     * 当前歌曲角标
     */
    const val EXTRA_POSITION = "Position"
    //_______________________________________|更新状态|______________________________________________________________________________________________________
    //更新正在播放歌曲
    const val UPDATE_META_DATA = 1002

    //更新播放状态
    const val UPDATE_PLAY_STATE = 1003
    //更新正在播放歌曲

    //_______________________________________|歌手写真|______________________________________________________________________________________________________
    /**
     * 歌手id
     */
    const val SINGER_ID = "Singer_ID"

    //_______________________________________|歌曲属性|______________________________________________________________________________________________________
    //歌曲名字
    const val SONG_NAME = "SongName"

    //歌曲 歌手名字
    const val SINGER_NAME = "SingerName"
//_______________________________________|回调|______________________________________________________________________________________________________

    //媒体数据库变化
    const val MEDIA_STORE_CHANGE = "${PACKAGE_NAME}.media_store.change"

    //读写权限变化
    const val PERMISSION_CHANGE = "${PACKAGE_NAME}.permission.change"

    //播放列表变换
    const val PLAYLIST_CHANGE = "${PACKAGE_NAME}.playlist.change"

    //播放数据变化
    const val PLAY_DATA_CHANGES = "${PACKAGE_NAME}.meta.change"

    //播放状态变化
    const val PLAY_STATE_CHANGE = "${PACKAGE_NAME}.play_state.change"

    //歌曲标签变化
    const val TAG_CHANGE = "${PACKAGE_NAME}.tag_change"

    //操作命令
    const val ACTION_CMD = "$PACKAGE_NAME.cmd"
//_______________________________________|音乐平台|______________________________________________________________________________________________________
    const val KEY_MUSIC_PLATFORM="MusicPlatform"
    const val KU_GOU=1
    const val HIF_INI=2

    const val NEW_SONG_KU_GOU = 3
}