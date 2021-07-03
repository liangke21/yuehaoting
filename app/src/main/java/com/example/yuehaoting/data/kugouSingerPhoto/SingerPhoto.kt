package com.example.yuehaoting.data.kugouSingerPhoto

/**
 * Created by 13967 on 2021-06-27
 */
data class SingerPhoto(
    var status: Int,
    var error_code: Int,
    var data: List<List<Data>>
) {
    data class Data(
        var author_id: String,
        var author_name: String,
        var is_publish: String,
        var res_hash: String,
        var avatar: String,
        var sizable_avatar: String,
        var imgs: Imgs
    ) {
        data class Imgs(
            var `2`: List<Data2>,
            var `3`: List<Data3>,
            var `4`: List<Data4>,
            var `5`: List<Data5>,
            var `8`: List<Data8>
        ) {
            data class Data2(
                var file_hash: String,
                var sizable_portrait: String,
                var filename: String
            )

            data class Data3(
                var file_hash: String,
                var sizable_portrait: String,
                var filename: String
            )

            data class Data4(
                var file_hash: String,
                var sizable_portrait: String,
                var filename: String
            )

            data class Data5(
                var file_hash: String,
                var sizable_portrait: String,
                var filename: String
            )

            data class Data8(
                var file_hash: String,
                var sizable_portrait: String,
                var filename: String
            )
        }
    }
}