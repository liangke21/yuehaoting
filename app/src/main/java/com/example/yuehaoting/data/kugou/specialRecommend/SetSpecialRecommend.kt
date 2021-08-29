package com.example.yuehaoting.data.kugou.specialRecommend

/**
 * 请求头
 * 手机app 特别推荐数据
 */
data class SetSpecialRecommend(
    val apiver: Int?,
    val appid: Int?,
    val client_playlist: List<Any?>?,
    val client_playlist_flag: Int?,
    val clienttime: Int?,
    val clientver: Int?,
    val key: String?,
    val mid: String?,
    val module_id: Int?,
    val platform: String?,
    val req_multi: Int?,
    val session: String?,
    val special_list: List<Special?>?,
    val theme_last_showtime: Int?,
    val userid: String?
) {
    data class Special(
        val A: Int?,
        val F: Int?,
        val ID: Int?,
        val T: Int?
    )
}