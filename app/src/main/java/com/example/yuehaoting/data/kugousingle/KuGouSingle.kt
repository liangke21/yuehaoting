package com.example.yuehaoting.data.kugousingle

/**
 * Created by 13967 on 2021-06-03
 */
data class KuGouSingle(
    var status: Int,
    var data: Data,
    var error_code: Int,
    var error_msg: String
) {
    data class Data(
        var searchfull: Int,
        var total: Int,
        var istagresult: Int,
        var isshareresult: Int,
        var page: Int,
        var chinesecount: Int,
        var correctiontype: Int,
        var allowerr: Int,
        var correctionsubject: String,
        var subjecttype: Int,
        var pagesize: Int,
        var istag: Int,
        var correctiontip: String,
        var correctionforce: Int,
        var sectag_info: SectagInfo,
        var aggregation: List<Aggregation>,
        var lists: List<Lists>
    ) {
        data class SectagInfo(var is_sectag: Int)
        data class Aggregation(
            var key: String,
            var count: Int
        )

        data class Lists(
            var Suffix: String,
            var SongName: String,
            var OwnerCount: Int,
            var MvType: Int,
            var TopicRemark: String,
            var SQFailProcess: Int,
            var Source: String,
            var Bitrate: Int,
            var HQExtName: String,
            var SQFileSize: Int,
            var Accompany: Int,
            var AudioCdn: Int,
            var MvTrac: Int,
            var SQDuration: Int,
            var recommend_type: Int,
            var ExtName: String,
            var Auxiliary: String,
            var SQPkgPrice: Int,
            var Category: Int,
            var Scid: Int,
            var OriSongName: String,
            var Uploader: String,
            var SQBitrate: Int,
            var HQBitrate: Int,
            var Audioid: Int,
            var HiFiQuality: Int,
            var OriOtherName: String,
            var AlbumPrivilege: Int,
            var TopicUrl: String,
            var SuperFileHash: String,
            var OldCpy: Int,
            var IsOriginal: Int,
            var Privilege: Int,
            var TagContent: String,
            var ResBitrate: Int,
            var FileHash: String,
            var SQPayType: Int,
            var trans_param: TransParam,
            var HQPrice: Int,
            var FoldType: Int,
            var Type: String,
            var AlbumID: String,
            var SQExtName: String,
            var AlbumName: String,
            var FileName: String,
            var MixSongID: String,
            var ID: String,
            var SuperFileSize: Int,
            var SQPrivilege: Int,
            var SQFileHash: String,
            var SuperExtName: String,
            var HQPrivilege: Int,
            var SuperBitrate: Int,
            var SuperDuration: Int,
            var HQPkgPrice: Int,
            var ResFileHash: String,
            var FileSize: Int,
            var ResFileSize: Int,
            var HQFileHash: String,
            var SongLabel: String,
            var PublishTime: String,
            var Publish: Int,
            var mvTotal: Int,
            var MvHash: String,
            var PkgPrice: Int,
            var M4aSize: Int,
            var Duration: Int,
            var OtherName: String,
            var PublishAge: Int,
            var SQPrice: Int,
            var ResDuration: Int,
            var Price: Int,
            var FailProcess: Int,
            var SingerName: String,
            var HQFailProcess: Int,
            var HQFileSize: Int,
            var HQPayType: Int,
            var HQDuration: Int,
            var PayType: Int,
            var HasAlbum: Int,
            var QualityLevel: Int,
            var SourceID: Int,
            var mGrp: List<Grp>
        ) {


            data class TransParam(
                var cpy_grade: Int,
                var musicpack_advance: Int,
                var cpy_level: Int,
                var hash_offset: HashOffset,
                var pay_block_tpl: Int,
                var cid: Int,
                var display_rate: Int,
                var appid_block: String,
                var display: Int,
                var cpy_attr0: Int
            ) {
                data class HashOffset(
                    var file_type: Int,
                    var start_byte: Int,
                    var end_ms: Int,
                    var offset_hash: String,
                    var start_ms: Int,
                    var end_byte: Int
                )
            }

            data class Grp(
                var Suffix: String,
                var SongName: String,
                var OwnerCount: Int,
                var MvType: Int,
                var TopicRemark: String,
                var SQFailProcess: Int,
                var Source: String,
                var Bitrate: Int,
                var HQExtName: String,
                var SQFileSize: Int,
                var Accompany: Int,
                var AudioCdn: Int,
                var MvTrac: Int,
                var SQDuration: Int,
                var recommend_type: Int,
                var ExtName: String,
                var Auxiliary: String,
                var SQPkgPrice: Int,
                var Category: Int,
                var Scid: Int,
                var OriSongName: String,
                var Uploader: String,
                var SQBitrate: Int,
                var HQBitrate: Int,
                var Audioid: Int,
                var HiFiQuality: Int,
                var OriOtherName: String,
                var AlbumPrivilege: Int,
                var TopicUrl: String,
                var SuperFileHash: String,
                var ASQPrivilege: Int,
                var OldCpy: Int,
                var IsOriginal: Int,
                var Privilege: Int,
                var TagContent: String,
                var ResBitrate: Int,
                var FileHash: String,
                var SQPayType: Int,
                var trans_param: TransParam,
                var HQPrice: Int,
                var Type: String,
                var A320Privilege: Int,
                var AlbumID: String,
                var SQExtName: String,
                var AlbumName: String,
                var FileName: String,
                var MixSongID: String,
                var ID: String,
                var SuperFileSize: Int,
                var SQPrivilege: Int,
                var SQFileHash: String,
                var SuperExtName: String,
                var HQPrivilege: Int,
                var SuperBitrate: Int,
                var SuperDuration: Int,
                var HQPkgPrice: Int,
                var ResFileHash: String,
                var FileSize: Int,
                var ResFileSize: Int,
                var HQFileHash: String,
                var SongLabel: String,
                var PublishTime: String,
                var Publish: Int,
                var mvTotal: Int,
                var MvHash: String,
                var PkgPrice: Int,
                var M4aSize: Int,
                var Duration: Int,
                var OtherName: String,
                var PublishAge: Int,
                var SQPrice: Int,
                var ResDuration: Int,
                var Price: Int,
                var FailProcess: Int,
                var SingerName: String,
                var HQFailProcess: Int,
                var HQFileSize: Int,
                var HQPayType: Int,
                var HQDuration: Int,
                var PayType: Int,
                var HasAlbum: Int,
                var QualityLevel: Int,
                var SourceID: Int,
                var SingerId: List<Int>
            ) {
                data class TransParam(
                    var cpy_grade: Int,
                    var musicpack_advance: Int,
                    var cpy_level: Int,
                    var hash_offset: HashOffset,
                    var pay_block_tpl: Int,
                    var cpy_attr0: Int,
                    var display_rate: Int,
                    var appid_block: String,
                    var display: Int,
                    var cid: Int
                ) {
                    data class HashOffset(
                        var file_type: Int,
                        var start_byte: Int,
                        var start_ms: Int,
                        var offset_hash: String,
                        var end_ms: Int,
                        var end_byte: Int
                    )
                }
            }
        }
    }
}