package com.example.yuehaoting.util

/**
 * 作者: LiangKe
 * 时间: 2021/9/21 10:40
 * 描述:
 */
object MyUtil {


    /**
     * 时间转换
     * @param duration Long
     * @return String 00:00格式的时间
     */
    fun getTime(duration: Long): String {
        val minute = duration.toInt() / 1000 / 60
        val second = (duration / 1000).toInt() % 60
        //如果分钟数小于10
        return if (minute < 10) {
            if (second < 10) {
                "0$minute:0$second"
            } else {
                "0$minute:$second"
            }
        } else {
            if (second < 10) {
                "$minute:0$second"
            } else {
                "$minute:$second"
            }
        }
    }

    fun getSecond(duration: Int): Int {
        return duration /1000
    }

}