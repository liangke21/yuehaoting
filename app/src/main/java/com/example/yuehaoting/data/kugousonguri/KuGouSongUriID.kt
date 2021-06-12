package com.example.yuehaoting.data.kugousonguri

/**
 * Created by 13967 on 2021-06-12
 */
data class KuGouSongUriID(
    var status: Int,
    var err_code: Int,
    var data: Data
) {
    data class Data(
        var album_id: String,
    )
}