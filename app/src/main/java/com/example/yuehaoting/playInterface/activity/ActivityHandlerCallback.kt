package com.example.yuehaoting.playInterface.activity

/**
 * 作者: LiangKe
 * 时间: 2021/9/21 19:16
 * 描述: PlayActivity 和 PlayActivityHandler 交互回调的接口
 */
interface ActivityHandlerCallback {

    fun onUpdateProgressByHandler()

    fun onUpdateSeekBarByHandler()
}