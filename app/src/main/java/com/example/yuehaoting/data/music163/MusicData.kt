package com.example.yuehaoting.data.music163

/**
 * Created by 13967 on 2021-08-01
 */
data class MusicData(
    var code: Int?,
    var error: String?,
    var data: List<Data>


) {
    data class Data(
        var type: String?,
        var link: String?,
        var songid: Int?,
        var title: String?,
        var author: String?,
        var lrc: String?,
        var url: String?,
        var pic: String?
    )
}